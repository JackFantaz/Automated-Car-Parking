%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "192.168.1.191",  "TCP", "60000").
context(ctxsonar, "localhost",  "TCP", "60001").
 qactor( sonaractor, ctxsonar, "it.unibo.sonaractor.Sonaractor").
