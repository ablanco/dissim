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

__jade = "java -cp '/usr/share/eclipse/plugins/it.fbk.sra.ejade_0.8.0/lib/libjade/*:/opt/lib/pfc/*:.' jade.Boot -gui -host klpt-chakra "
__creator = "God:agents.CreatorAgent"

def __toLatLng(text):
    coord = text.split(',')
    coord[0] = float(coord[0])
    coord[1] = float(coord[1])
    return coord

def __args(args):
    res = "\\("
    for a in args:
        res = res + a + " "
    res = res + "\\)"
    return res

if len(sys.argv) == 4:
    os.system(__jade + __creator + __args(sys.argv[1:4]))
else:
    NW = raw_input('Coordenadas (Lat,Lng) NorOeste del área de simulación: ')
    NW = __toLatLng(NW)
    SE = raw_input('Coordenadas (Lat,Lng) SudEste del área de simulación: ')
    SE = __toLatLng(SE)
    tileSize = raw_input('Diámetro (en metros) de la circunferencia que circunscribe a los hexágonos: ')
    nenvs = raw_input('Número de agentes entorno: ')
    nws = raw_input('Número de entradas de agua: ')
    ws = []
    for i in range(nws):
        tws = raw_input('Coordenadas (Lat,Lng) de la entrada de agua ' + str(i) + ': ')
        ws.append(__toLatLng(WS))
    # TODO
    os.system(__jade + __creator + __args()