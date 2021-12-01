Write-Host "Cleaning..."
mvn clean
git clean -fxd
Write-Host "Checkout..."
git pull
Write-Host "Building..."
# start /B /WAIT
mvn -D"dependency-check.skip"=true -DskipTests package
Write-Host "Removing old war..."
Remove-Item ./install/docker/target/dependency/airsonic.war -ErrorAction:SilentlyContinue
Remove-Item ./install/docker/target/dependency/airsonic-main.war -ErrorAction:SilentlyContinue
Write-Host "Copying new war..."
New-Item ./install/docker/target/dependency -ItemType Directory
Copy-Item ./airsonic-main/target/airsonic.war -Destination ./install/docker/target/dependency/airsonic-main.war
Write-Host "Cleaning..."
# start /B /WAIT
mvn clean
Write-Host "Done."