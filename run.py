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
import math

__jade = "java -cp '" + os.environ['JADE_HOME'] + "/*:" + os.environ['JAK_HOME'] + "/*:.' jade.Boot"
__host = "-host " + socket.gethostname()
__port = None
__local_host = None
__local_port = None
__container = None
__name = '-name DisSim'
__creator = "God:agents.CreatorAgent"
__configPath = os.environ['HOME'] + "/.dissim/"
__scenPath = __configPath + "scen/" # default

# DEFINICIÓN DE FUNCIONES

def printUsage():
    print('Modo de empleo: ' + sys.argv[0] + ' [OPCIONES] [FICHERO]')
    print('\nOpciones:')
    print('\t--help\t\t\t\tMuestra esta ayuda\n\t-h\n')
    print('\t--add NOMBRE:CLASE PARAMETRO+\tAñade un nuevo agente a una plataforma\n\t-a NOMBRE:CLASE PARAMETRO+\n')
    print('\t--gui\t\t\t\tLanza también la interfaz gráfica de JADE\n\t-g\n')
    print('\t--name NOMBRE\t\t\tPermite establecer el nombre de la plataforma JADE (por defecto DisSim)\n\t-n NOMBRE\n')
    print('\t--host HOST\t\t\tPermite establecer el host del contenedor principal\n\t-H HOST\n')
    print('\t--port PUERTO\t\t\tPermite establecer el puerto a través del cual conectarse al contenedor principal\n\t-p PUERTO\n')
    print('\t--local-host HOST\t\tPermite establecer el host del contenedor\n\t-lh HOST\n')
    print('\t--local-port PUERTO\t\tPermite establecer el puerto a través del cual conectarse al contenedor\n\t-lp PUERTO\n')
    print('\t--container\t\t\tEspecifica que esta instancia de JADE es un contenedor\n\t-c')
    print('\nSi no se le pasa ningún argumento se ejecutará el modo interactivo')
    print('para la generación de un nuevo escenario de simulación\n')

def launch(scen, agt=None, args=None):
    global __jade
    __jade = __jade + " " + __name + " " + __host
    if __port:
        __jade = __jade + " " + __port
    if __local_port:
        __jade = __jade + " " + __local_port
    if __local_host:
        __jade = __jade + " " + __local_host
    if __container:
        __jade = __jade + " " + __container
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
                __container = '-container'
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
                i = i + 1
                __name = '-name ' + sys.argv[i]
            if op == '--gui' or op == '-g':
                __jade = __jade + ' -gui'
            if op == '--port' or op == '-p':
                i = i + 1
                __port = '-port ' + sys.argv[i]
            if op == '--host' or op == '-H':
                i = i + 1
                __host = '-host ' + sys.argv[i]
            if op == '--local-port' or op == '-lp':
                i = i + 1
                __local_port = '-local-port ' + sys.argv[i]
            if op == '--local-host' or op == '-lh':
                i = i + 1
                __local_host = '-local-host ' + sys.argv[i]
            if op == '--container' or op == '-c':
                __container = '-container'
        else:
            scen = op
        i = i + 1
    if scen:
        if not(scen.endswith('.scen')):
            scen = scen + '.scen'
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
    fich.write('\nNW=[' + NW + ']')
    SE = raw_input('Coordenadas (con el formato Lat,Lng) SudEste del área de simulación: ')
    fich.write('\nSE=[' + SE + ']')
    tileSize = raw_input('Diámetro (en metros) de la circunferencia que circunscribe a los hexágonos: ')
    fich.write('\ntileSize=' + tileSize)
    precision = raw_input('Precisión en altura (1 una unidad de altura equivale a 1/precision metros): ')
    fich.write('\nprecision=' + precision)
    nenvs = raw_input('Número de agentes entorno: ')
    fich.write('\nnumEnvs=' + nenvs)
    rndTerrain = raw_input('¿Terreno aleatorio? (True|False): ')
    fich.write('\nrandomTerrain=' + rndTerrain)
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
        fich.write('\nwaterSource=[' + ws)
        wws = int(raw_input('Cantidad de agua de dicha entrada (en litros por ' + timeRealWS + ' minutos): '))
        wws = wws / 1000 # paso a metros cúbicos
        area = (3 * math.sqrt(3) * ((int(tileSize)/2)**2)) / 2 # área del hexágono
        h = wws / area # altura en metros
        wws = int(round(h * int(precision))) # altura en unidades de altura
        if wws < 1:
            wws = 1
        fich.write(',' + str(wws) + ']')
    print('\nPERSONAS\n')
    timePeople = raw_input('Tiempo (en milisegundos) entre actualizaciones de los agentes humanos: ')
    fich.write('\nupdateTimePeople=' + timePeople)
    npeople = int(raw_input('Número de grupos de agentes humanos en la simulación: '))
    for i in range(npeople):
        person = raw_input('Coordenadas (Lat,Lng) del peatón ' + str(i) + ': ')
        fich.write('\nperson=[' + person)
        person = raw_input('Distancia de visión del peatón (en hexágonos): ')
        fich.write(',' + person)
        person = raw_input('Velocidad (distancia en hexágonos a la que es capaz de moverse en un paso) del peatón: ')
        fich.write(',' + person)
        person = raw_input('Número de clones (agentes en el grupo): ')
        fich.write(',' + person + ',[')
        first = True
        while person != '':
            person = raw_input('Coordenadas (Lat,Lng) de un refugio objetivo (deja en blanco para no añadir más): ')
            if person != '' and first:
                fich.write(person)
                first = False
            elif person != '':
                fich.write(',' + person)
        fich.write(']]')
    print('\nVISORES\n')
    timeKml = raw_input('Período (en milisegundos) de actualización del generador de KML: ')
    fich.write('\nupdateTimeKml=' + timeKml)
    timeVisor = raw_input('Período (en milisegundos) de actualización de los visores: ')
    fich.write('\nupdateTimeVisor=' + timeVisor)
    print('\nESCENARIO GENERADO\n')
    fich.close()
    # lanzamos la simulación con el fichero recién generado
    launch(scen)