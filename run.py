#!/usr/bin/python
# -*- coding: utf-8 -*-

    #Flood and evacuation simulator using multi-agent technology
    #Copyright (C) 2010 Alejandro Blanco and Manuel Gomar

    #This program is free software: you can redistribute it and/or modify
    #it under the terms of the GNU General Public License as published by
    #the Free Software Foundation, either version 3 of the License, or
    #(at your option) any later version.

    #This program is distributed in the hope that it will be useful,
    #but WITHOUT ANY WARRANTY; without even the implied warranty of
    #MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    #GNU General Public License for more details.

    #You should have received a copy of the GNU General Public License
    #along with this program.  If not, see <http://www.gnu.org/licenses/>.

import sys
import os
import socket

__jade = "java -cp '" + os.environ['JADE_HOME'] + "/*:" + os.environ['DISSIM_HOME'] + "/lib/*:.' jade.Boot -host " + socket.gethostname()
__creator = "God:agents.CreatorAgent"
__configPath = os.environ['HOME'] + "/.dissim/"
__scenPath = __configPath + "scen/" # default
__name = 'DisSim'

# DEFINICIÓN DE FUNCIONES

def printUsage():
    print('Modo de empleo: ' + sys.argv[0] + ' [OPCIONES] [FICHERO]')
    print('\nOpciones:')
    print('\t--help\t\t\t\tMuestra esta ayuda\n\t-h\n')
    print('\t--gui\t\t\t\tLanza también la interfaz gráfica de JADE\n\t-g\n')
    print('\t--name NOMBRE\t\t\tPermite establecer el nombre de la plataforma JADE (por defecto DisSim)\n\t-n NOMBRE\n')
    print('\t--add CLASE PARAMETRO+\t\tAñade un nuevo agente a una plataforma\n\t-a CLASE PARAMETRO+\n')
    print('\nSi no se le pasa ningún argumento se ejecutará el modo interactivo')
    print('para la generación de un nuevo escenario de simulación\n')

def launch(scen, agt=None, args=None):
    global __jade
    __jade = __jade + " -name " + __name
    if agt:
        com = __jade + " " + agt + "\\("
        for a in args:
            com = com + a + ' '
        com = com + "\\)"
        os.system(com)
    else:
        if os.access(scen, os.F_OK):
            os.system(__jade + " " + __creator + "\\(" + scen + "\\)")
        else:
            print('ERROR: El fichero ' + scen + ' no existe o no es accesible.')

# MAIN

# si no existe ya creamos el directorio de configuración
if not(os.access(__configPath, os.F_OK)):
    os.makedirs(__configPath)
# creamos el fichero de configuración si no existe
if not(os.access(__configPath + "config.conf", os.F_OK)):
    fich = open(__configPath + 'config.conf', 'w')
    fich.write('scenPath=' + __scenPath)
    fich.close()
else:
    # cargamos la configuración
    fich = open(__configPath + 'config.conf', 'r')
    data = fich.readlines()
    for d in data:
        d = d.replace('\n','')
        pair = d.split('=')
        if pair[0] == 'scenPath':
            __scenPath = pair[1]
    fich.close()
# si no existe ya creamos el directorio con los escenarios
if not(os.access(__scenPath, os.F_OK)):
    os.makedirs(__scenPath)

if len(sys.argv) > 1:
    # no hay que generar un nuevo escenario
    i = 1
    scen = None
    agt = None
    args = []
    # procesar los argumentos
    while i < len(sys.argv):
        # el primer argumento es el nombre del programa, nos lo saltamos
        op = sys.argv[i]
        if op.startswith('-'):
            if op == '--help' or op == '-h':
                printUsage()
            if op == '--add' or op == '-a':
                i = i + 1
                agt = sys.argv[i]
                i = i + 1
                arg = sys.argv[i]
                while not(arg.startswith('-')):
                    args.append(arg)
                    i = i + 1
                    if i < len(sys.argv):
                        arg = sys.argv[i]
                    else:
                        break
            if op == '--name' or op == '-n':
                i == i + 1
                __name = sys.argv[i]
            if op == '--gui' or op == '-g':
                __jade = __jade + ' -gui'
        else:
            scen = op
        i = i + 1
    if scen:
        # nos pasan el nombre del fichero con el escenario por parámetros
        if scen.find('/') > 0:
            # es una ruta completa
            launch(scen)
        else:
            # es sólo el nombre del fichero, así que lo buscamos
            # en la ruta por defecto
            launch(__scenPath + scen)
    elif agt:
        launch(None, agt, args)
else:
    print('GENERANDO UN NUEVO ESCENARIO DE SIMULACIÓN\n')
    simName = raw_input('Nombre del nuevo escenario: ')
    scen = __scenPath + simName + '.scen'
    fich = open(scen, "w")
    fich.write('type=util.flood.FloodScenario')
    fich.write('\nname=' + simName)
    # preguntamos los datos del escenario
    desc = raw_input('Descripción del escenario: ')
    fich.write('\ndescription=' + desc)
    time = raw_input('Fecha y hora (con el formato dd/mm/aaaa-hh:mm:ss) de comienzo de la simulación: ')
    time = time.split('-')
    fich.write('\ndate=' + time[0])
    fich.write('\nhour=' + time[1])
    print('\nÁREA DE LA SIMULACIÓN\n')
    NW = raw_input('Coordenadas (con el formato Lat,Lng) NorOeste del área de simulación: ')
    NW = NW.split(',')
    fich.write('\nNW=[' + NW[0] + ',' + NW[1] + ']')
    SE = raw_input('Coordenadas (con el formato Lat,Lng) SudEste del área de simulación: ')
    SE = SE.split(',')
    fich.write('\nSE=[' + SE[0] + ',' + SE[1] + ']')
    tileSize = raw_input('Diámetro (en metros) de la circunferencia que circunscribe a los hexágonos: ')
    fich.write('\ntileSize=' + tileSize)
    nenvs = raw_input('Número de agentes entorno: ')
    fich.write('\nnumEnvs=' + nenvs)
    print('\nENTRADA DE AGUA\n')
    timeFlood = raw_input('Período (en milisegundos) de la actualización del agua en el terreno: ')
    fich.write('\nupdateTimeFlood=' + timeFlood)
    timeWS = raw_input('Tiempo (en milisegundos) entre entradas de nueva agua en la inundación: ')
    fich.write('\nupdateTimeWS=' + timeWS)
    timeRealWS = raw_input('Tiempo real dentro de la simulación (en minutos) que representa el tiempo anterior: ')
    fich.write('\nupdateTimeRealWS=' + timeRealWS)
    nws = int(raw_input('Número de entradas de agua: '))
    for i in range(nws):
        ws = raw_input('Coordenadas (Lat,Lng) de la entrada de agua ' + str(i) + ': ')
        ws = ws.split(',')
        fich.write('\nwaterSource=[' + ws[0] + ',' + ws[1] + ',')
        wws = raw_input('Cantidad de agua de dicha entrada: ')
        fich.write(wws + ']')
    print('\nPERSONAS\n')
    timePeople = raw_input('Tiempo (en milisegundos) entre actualizaciones de los agentes humanos: ')
    fich.write('\nupdateTimePeople=' + timePeople)
    npeople = int(raw_input('Número de agentes humanos en la simulación: '))
    for i in range(npeople):
        person = raw_input('Coordenadas (Lat,Lng) del peatón ' + str(i) + ': ')
        person = person.split(',')
        fich.write('\nperson=[' + person[0] + ',' + person[1])
        person = raw_input('Distancia de visión del peatón: ')
        fich.write(',' + person)
        person = raw_input('Velocidad (distancia a la que es capaz de moverse en un paso) del peatón: ')
        fich.write(',' + person + ']')
    print('\nESCENARIO GENERADO\n')
    fich.close()
    # lanzamos la simulación con el fichero recién generado
    launch(scen)