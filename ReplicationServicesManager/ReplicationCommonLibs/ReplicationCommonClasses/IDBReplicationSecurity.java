package ReplicationCommonClasses;

import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public interface IDBReplicationSecurity {

    public boolean loadConfig( String strRunningPath, String strConfigFile, CExtendedLogger Logger, CLanguage Lang );
	
	public boolean checkUser( String strUser, String strPassword, CExtendedLogger Logger, CLanguage Lang );
	
	public String getConfigFile();
	
}
