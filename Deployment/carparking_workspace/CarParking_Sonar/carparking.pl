%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "172.16.0.2",  "TCP", "60000").
context(ctxsonar, "localhost",  "TCP", "60001").
 qactor( sonaractor, ctxsonar, "it.unibo.sonaractor.Sonaractor").
