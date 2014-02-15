package ReplicationCommonClasses;

import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public interface IDBReplicationWorker {

    public boolean loadConfig( String strRunningPath, String strConfigFile, CExtendedLogger Logger, CLanguage Lang );
	
	public String getConfigFile();
    
}
