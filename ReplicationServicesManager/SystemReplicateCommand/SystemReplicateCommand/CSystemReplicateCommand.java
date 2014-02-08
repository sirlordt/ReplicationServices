package SystemReplicateCommand;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import AbstractResponseFormat.CAbstractResponseFormat;
import AbstractService.CAbstractService;
import ReplicationCommonClasses.CReplicationAbstractService;

public class CSystemReplicateCommand extends CReplicationAbstractService {

	@Override
	public int executeService(int intEntryCode, HttpServletRequest Request, HttpServletResponse Response, String strSecurityTokenID, HashMap<String, CAbstractService> RegisteredServices, CAbstractResponseFormat ResponseFormat, String strResponseFormatVersion ) {
		// TODO Auto-generated method stub
		return 0;
	}

}
