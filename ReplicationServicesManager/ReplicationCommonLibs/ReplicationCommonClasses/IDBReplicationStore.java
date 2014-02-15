package ReplicationCommonClasses;

import java.io.InputStream;
import java.util.LinkedList;

import CommonClasses.CExpresionsFilters;
import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public interface IDBReplicationStore {

    public boolean loadConfig( String strRunningPath, String strStoreDir, String strName, String strTargeDatabase, String strConfigFile, CExtendedLogger Logger, CLanguage Lang );

	public String getName();

	public String getTargeDatabase();

	public String getConfigFile();
    
	public LinkedList<String> buildReplicationStoreFilesIndex( String strStoreDir, CExtendedLogger Logger, CLanguage Lang );
	
	public boolean addReplicationCommandBlockToStore( CReplicationCommandBlock ReplicationCommandBlock, CExtendedLogger Logger, CLanguage Lang );

	public LinkedList<String> getAllDataBlocksID( String strDataBlockID, CExtendedLogger Logger, CLanguage Lang );
	
	public String getNextDataBlockID( String strDataBlockID, CExtendedLogger Logger, CLanguage Lang );

	public LinkedList<String> getCommandBlocksIDInDataBlockID( String strDataBlockID, CExtendedLogger Logger, CLanguage Lang );
	
	public LinkedList<CReplicationCommandBlock> getCommandBlocksInDataBlockID( String strDataBlockID, CExtendedLogger Logger, CLanguage Lang );

	public CReplicationCommandBlock getCommandBlock( String strDataBlockID, String strCommandBlockID, CExtendedLogger Logger, CLanguage Lang );

	public CReplicationCommandBlock getNextCommandBlock( String strDataBlockID, String strCommandBlockID, CExtendedLogger Logger, CLanguage Lang );
	
	//RandomAccessFile
	public InputStream getStreamDataBlock( String strDataBlockID, CExtendedLogger Logger, CLanguage Lang );
	
	public IDBReplicationSecurity getDBReplicationSecurity();
	public void setDBReplicationSecurity( IDBReplicationSecurity DBReplicationSecurity );

	public IDBReplicationWorker getPullDBReplicationWorker();
	public void setPullReplicationWorker( IDBReplicationWorker PullDBReplicationWorker );
	
	public IDBReplicationWorker getPushDBReplicationWorker();
	public void setPushReplicationWorker( IDBReplicationWorker PushDBReplicationWorker );
	
	public CExpresionsFilters getFilters();
	public void setFilters( CExpresionsFilters ExpFilters );
	
	public void publishDataBlockToStoreIndex( String strDataBlockFileName, CExtendedLogger Logger, CLanguage Lang );
	
}
