# CCS - Milner's Calculus of Communicating Systems

Simple animator of CCS's core terms.
A snapshot of the compiled website can be found in the `docs` folder, and used online via the link below.

 - http://lmf.di.uminho.pt/ccs-caos/


# Caos

This project uses the Caos's framework. More information on it can be found online:

 - Caos' GitHub page: https://github.com/arcalab/CAOS
 - Caos' tutorial: https://arxiv.org/abs/2304.14901
 - Caos' demo video: https://youtu.be/Xcfn3zqpubw 


## Requirements

- JVM (>=1.8)
- sbt

## Compilation

You need to get the submodules dependencies (CAOS library), and later compile using ScalaJS.
The result will be a JavaScript file that is already being imported by an existing HTML file. 

1. `git submodule update --init`
2. `sbt fastLinkJS`
3. open the file `lib/tool/index.html`
