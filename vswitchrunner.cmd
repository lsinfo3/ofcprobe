@echo off
for /L %%a in (1, 2, 100) do java -jar ofcprobe.jar config.ini %%a