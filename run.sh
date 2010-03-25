#!/bin/sh

LIB=lib

if test -d $LIB
then
else
    # TODO
fi

java -cp '\''/usr/share/eclipse/plugins/it.fbk.sra.ejade_0.8.0/lib/libjade/*:/opt/lib/pfc/*:.'\'' jade.Boot -gui -host klpt-chakra