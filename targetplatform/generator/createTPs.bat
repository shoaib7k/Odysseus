REM Simple windows script to create the required target platforms

mkdir generated

del generated\platform_core.target
type base\tp_header.xml base\core_locations.xml base\ody_resources_location.xml base\tp_footer.xml > tmp.target
powershell -Command "(gc tmp.target) -replace '__TITLE__', '(Core)' | Out-File -encoding ASCII generated\platform_core.target"

del generated\platform_master_stable.target 
type base\tp_header.xml base\core_locations.xml base\ody_resources_location.xml base\ody_stable_locations.xml base\tp_footer.xml > tmp.target
powershell -Command "(gc tmp.target) -replace '__TITLE__', '(master stable)' | Out-File -encoding ASCII tmp.target"
powershell -Command "(gc tmp.target) -replace '__BRANCH__', 'master' | Out-File -encoding ASCII generated\platform_master_stable.target"
del tmp.target

del generated\platform_development_stable.target
type base\tp_header.xml base\core_locations.xml base\ody_resources_location.xml base\ody_stable_locations.xml base\tp_footer.xml > tmp.target
powershell -Command "(gc tmp.target) -replace '__TITLE__', '(development stable)' | Out-File -encoding ASCII tmp.target"
powershell -Command "(gc tmp.target) -replace '__BRANCH__', 'development'| Out-File -encoding ASCII generated\platform_development_stable.target"
del tmp.target

del generated\platform_master_all.target
type base\tp_header.xml base\core_locations.xml base\ody_resources_location.xml base\ody_stable_locations.xml base\ody_incubation_locations.xml base\tp_footer.xml > tmp.target
powershell -Command "(gc tmp.target) -replace '__TITLE__', '(master all)' | Out-File -encoding ASCII tmp.target"
powershell -Command "(gc tmp.target) -replace '__BRANCH__', 'master' | Out-File -encoding ASCII generated\platform_master_all.target"
del tmp.target

del generated\platform_development_all.target
type base\tp_header.xml base\core_locations.xml base\ody_resources_location.xml base\ody_stable_locations.xml base\ody_incubation_locations.xml base\tp_footer.xml > tmp.target
powershell -Command "(gc tmp.target) -replace '__TITLE__', '(development all)' | Out-File -encoding ASCII tmp.target"
powershell -Command "(gc tmp.target) -replace '__BRANCH__', 'development' | Out-File -encoding ASCII generated\platform_development_all.target"
del tmp.target


