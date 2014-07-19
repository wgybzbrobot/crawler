package com.zxsoft.crawler.protocol;

import java.net.URL;


public class ProtocolStatusUtils implements ProtocolStatusCodes {
  public static final ProtocolStatus STATUS_SUCCESS = makeStatus(SUCCESS);
  public static final ProtocolStatus STATUS_FAILED = makeStatus(FAILED);
  public static final ProtocolStatus STATUS_GONE = makeStatus(GONE);
  public static final ProtocolStatus STATUS_NOTFOUND = makeStatus(NOTFOUND);
  public static final ProtocolStatus STATUS_RETRY = makeStatus(RETRY);
  public static final ProtocolStatus STATUS_ROBOTS_DENIED = makeStatus(ROBOTS_DENIED);
  public static final ProtocolStatus STATUS_REDIR_EXCEEDED = makeStatus(REDIR_EXCEEDED);
  public static final ProtocolStatus STATUS_NOTFETCHING = makeStatus(NOTFETCHING);
  public static final ProtocolStatus STATUS_NOTMODIFIED = makeStatus(NOTMODIFIED);
  public static final ProtocolStatus STATUS_WOULDBLOCK = makeStatus(WOULDBLOCK);
  public static final ProtocolStatus STATUS_BLOCKED = makeStatus(BLOCKED);

  public static String getName(int code) {
    if (code == SUCCESS)
      return "SUCCESS";
    else if (code == FAILED)
      return "FAILED";
    else if (code == PROTO_NOT_FOUND)
      return "PROTO_NOT_FOUND";
    else if (code == GONE)
      return "GONE";
    else if (code == MOVED)
      return "MOVED";
    else if (code == TEMP_MOVED)
      return "TEMP_MOVED";
    else if (code == NOTFOUND)
      return "NOTFOUND";
    else if (code == RETRY)
      return "RETRY";
    else if (code == EXCEPTION)
      return "EXCEPTION";
    else if (code == ACCESS_DENIED)
      return "ACCESS_DENIED";
    else if (code == ROBOTS_DENIED)
      return "ROBOTS_DENIED";
    else if (code == REDIR_EXCEEDED)
      return "REDIR_EXCEEDED";
    else if (code == NOTFETCHING)
      return "NOTFETCHING";
    else if (code == NOTMODIFIED)
      return "NOTMODIFIED";
    else if (code == WOULDBLOCK)
      return "WOULDBLOCK";
    else if (code == BLOCKED)
      return "BLOCKED";
    return "UNKNOWN_CODE_" + code;
  }

  public static ProtocolStatus makeStatus(int code) {
	  ProtocolStatus pstatus = new ProtocolStatus();
	  pstatus.setCode(code);
	  return pstatus;
  }
  public static ProtocolStatus makeStatus(int code, URL u) {
    ProtocolStatus pstatus = new ProtocolStatus();
    pstatus.setU(u);
    pstatus.setCode(code);
    return pstatus;
  }
  
  public static ProtocolStatus makeStatus(int code, String message) {
	  ProtocolStatus pstatus = new ProtocolStatus();
	  pstatus.setMessage(message);
	  pstatus.setCode(code);
	  return pstatus;
  }


}
