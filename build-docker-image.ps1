# docker login
Write-Host "Logging into docker hub"
docker login --username subzeropl --password-stdin
# build image
Write-Host "Building image"
Set-Location ./install/docker
docker build -t subzeropl/images:airsonic_custom .
# push image
Write-Host "Pushing image to docker hub"
docker push subzeropl/images:airsonic_custom
# cleanup
Write-Host "Cleaning up"
docker image prune --all --force
Write-Host "Done."