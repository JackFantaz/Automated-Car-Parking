@echo off
START chrome "http://localhost:8100"
START chrome "http://localhost:8101"
DOCKER-COMPOSE -f carparking_webgui.yaml up
