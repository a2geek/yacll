# Overview

YACLL (Yet Another C-Like Language) is some early experimentation with ANTLR v2.
C syntax is simply used for familiarity.

# Code samples

There are a number of samples in the [samples](doc/samples) folder.

The libraries are available in the [resources](src/main/resources) folder.

# "Development Tool" Screen Shots

The YACLL "IDE" is a small persnickety editor (hint: use the menu to paste if you get duplicates).

![Editor](doc/images/sample-run-editor.png) 

When you run a program, it both draws in its own screen...

![Display](doc/images/sample-run-display.png)

... but it also displays all variables and the ending value:

![Variables](doc/images/sample-run-variables.png)

For those interested in the AST generated, that is also available:

![ANTLR Tree](doc/images/sample-run-antlr-tree.png)

# Build

Note that as part of migrating this into GitHub, Maven was added for dependency management as well as building.

It is very likely that, with these antiquated tools, `mvn generate-sources` needs to be executed to generate the ANTLR-related code.

After that, `mvn package` should build the JAR.  Note that some units currently do not work.
