Create all relevant Target platform files with this script (currently only windows version available). Generated TPs will be found in folder generated.

The base folder contains all templates to build the tp files:
- tp_header.xml: contains all header information including sequence number. Change sequencenumber if anything has changed in any other file
- core_locations.xml: Anything that is needed for ANY target platform must be placed here (e.g. eclipse components)
- ody_resources_locations.xml: Anything that comes from the odysseus resources update site 
- ody_stable_locations: Anything that comes from a stable branch (master and development)
- ody_incubation_locations: Anything that comes an incubatio branch (master and development) 
- tp-footer.xml: Should not be changed.
