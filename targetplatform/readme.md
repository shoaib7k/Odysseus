This folder contains different target platforms. You should choose the platform beloning to your kind of project

- *_stable: Contains all stable plugins. If you are developing for a stable plugin you should use this platform
- *_all: Contains all odysseus plugins (incubation and stable)
- core: For odysseus_core (no odysseus plugins)

In most cases you should use the master version. Only if you are developing against a plugin that is self not on the master or you are working on another plugin at the same time you should use development.

For maven you can use 

mvn clean verify -Dtargetfilename=platform_development_stable