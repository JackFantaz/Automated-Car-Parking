%====================================================================================
% carparking description   
%====================================================================================
context(ctxcarparking, "localhost",  "TCP", "60000").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( parkmanagerserviceactor, ctxcarparking, "it.unibo.parkmanagerserviceactor.Parkmanagerserviceactor").
  qactor( trolleyactor, ctxcarparking, "it.unibo.trolleyactor.Trolleyactor").
  qactor( parkserviceguiactor, ctxcarparking, "it.unibo.parkserviceguiactor.Parkserviceguiactor").
