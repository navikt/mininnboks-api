@echo off
title minehenvendelser FitNesse Server
setlocal

%~d0
cd %~p0

mvn test-compile exec:exec -Pfitnesse
