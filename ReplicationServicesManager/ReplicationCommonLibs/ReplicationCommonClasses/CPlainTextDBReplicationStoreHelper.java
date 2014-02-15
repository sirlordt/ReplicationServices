package ReplicationCommonClasses;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

import CommonClasses.CLanguage;
import ExtendedLogger.CExtendedLogger;

public class CPlainTextDBReplicationStoreHelper {

	/*protected static final String _Start_Command_Block = "<CommandBlock><";
	protected static final String _Transaction_ID = "TransactionID=";
	protected static final String _Command_ID = "CommandBlockID=";
	protected static final String _Init_Command = "[CommandInit]";
	protected static final String _End_Command = "[/CommandEnd]";
	protected static final String _Param = "Param=\"";
	protected static final String _Command_Block_Length = "CommandBlockLength=";
	protected static final String _End_Command_Block = "</CommandBlock>";*/
	protected static final String _XML_Header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
	protected static final String _Header = "<Header ";

	protected static final String _ReplicationStorageIndex = "ReplicationStorageFilesIndex.txt";

	File ReplicationStoreFilePath;
	
	SimpleDateFormat DF; 
	SimpleDateFormat TF; 
	
	String strHeaderData;
	int intHeaderLength;
	
	String strDataBlockID;
	
	protected Semaphore LockWriter;
	
	protected String strCurrentReplicationStoreFile;
	
	protected PrintWriter CurrentReplicationStoreFileWriter;

	protected CPlainTextDBReplicationStore PlainTextDBReplicationStore;
	
	//protected CConfigPlainTextDBReplicationStore ConfigPlainTextDBReplicationStore;
	
	public long lngStoreInitTime; 
	
	public long lngLastDataBlockWrite;
	
	public String strInstanceID;
	
	public CPlainTextDBReplicationStoreHelper( String strInstanceID, CPlainTextDBReplicationStore PlainTextDBReplicationStore, final CExtendedLogger Logger, final CLanguage Lang ) {
		
		try {
		
			this.PlainTextDBReplicationStore = PlainTextDBReplicationStore;
			
			LockWriter = new Semaphore( 1 );
			
			this.strInstanceID = strInstanceID;
			
			createDataBlockFileStore( true, Logger, Lang );

			DF = new SimpleDateFormat( "dd/MM/yyy" ); 
			TF = new SimpleDateFormat( "HH:mm:ss:SSS" ); 
			
			Timer checkToPublishDataBlockTimer = new Timer();
			
			final long lngMaxWaitToPublishDataBlock = PlainTextDBReplicationStore.ConfigPlainTextDBReplicationStore.lngMaxWaitToPublishDataBlock;
			
			TimerTask checkToPublishDataBlockTask = new TimerTask() {
		        
				public void run() {

					long lngCurrentTime = System.currentTimeMillis();
					
					//Check for fill velocity for store
					if ( lngStoreInitTime - lngCurrentTime >= lngMaxWaitToPublishDataBlock && lngLastDataBlockWrite >= 60000 ) {
						
						if ( ReplicationStoreFilePath.length() >= intHeaderLength ) {

							createDataBlockFileStore( true, Logger, Lang ); //Force create store file the store is filled to slow

						}
						
					}
					
				}
				
		     }; 
		     
			checkToPublishDataBlockTimer.scheduleAtFixedRate( checkToPublishDataBlockTask, PlainTextDBReplicationStore.ConfigPlainTextDBReplicationStore.lngMaxWaitToPublishDataBlock, PlainTextDBReplicationStore.ConfigPlainTextDBReplicationStore.lngMaxWaitToPublishDataBlock );
			
		}
		catch ( Error Err ) {

			if ( Logger != null )
				Logger.logError( "-1020", Err.getMessage(), Err );

		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1021", Ex.getMessage(), Ex );

		}
		
	}
	
	protected void createDataBlockFileStore( boolean bLockWriter, CExtendedLogger Logger, CLanguage Lang ) {
		
		try {

			if ( bLockWriter ) {
				
				LockWriter.acquire();
			
			}	
			
			if ( ReplicationStoreFilePath != null ) {
				  
				if ( CurrentReplicationStoreFileWriter != null ) {
					
					CurrentReplicationStoreFileWriter.print( "</BlockData>" ); //Close the file
					CurrentReplicationStoreFileWriter.flush();
					
				}
				
				//Publish the current store file changing the name from ????????.working to ????????.db
				ReplicationStoreFilePath.renameTo( new File( this.PlainTextDBReplicationStore.ConfigPlainTextDBReplicationStore.strStoreDir + this.PlainTextDBReplicationStore.ConfigPlainTextDBReplicationStore.strName + File.separatorChar + strDataBlockID + ".db" ) );

				this.PlainTextDBReplicationStore.publishDataBlockToStoreIndex( strDataBlockID + ".db", Logger, Lang );
				 
			}
			
			strDataBlockID = UUID.randomUUID().toString();  //Generate new uuid name for used in the file name

			strCurrentReplicationStoreFile = strDataBlockID + ".working";

			ReplicationStoreFilePath = new File( this.PlainTextDBReplicationStore.ConfigPlainTextDBReplicationStore.strStoreDir + this.PlainTextDBReplicationStore.ConfigPlainTextDBReplicationStore.strName + File.separatorChar + strCurrentReplicationStoreFile );

			CurrentReplicationStoreFileWriter = new PrintWriter( new FileOutputStream( ReplicationStoreFilePath ) );

			strHeaderData = _XML_Header;
			strHeaderData += "<DataBlock>\n";
			strHeaderData += _Header + "CreatedDate=\"" + DF.format( new Date() ) + "\" CreatedTime=\"" + TF.format( new Date() ) + "\" InstanceID=\"" + strInstanceID + "\" DataBlockID=\"" + strDataBlockID + "\" />\n"; 
			
			intHeaderLength = strHeaderData.length();	

			lngStoreInitTime = System.currentTimeMillis();  //Mark the init time for the file 

			CurrentReplicationStoreFileWriter.println( strHeaderData );

		}
		catch ( Error Err ) {

			if ( Logger != null )
				Logger.logError( "-1020", Err.getMessage(), Err );

		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1021", Ex.getMessage(), Ex );

		}
		finally {
			
			if ( bLockWriter ) {
				
				LockWriter.release();
				
			}	
			
		}
			
	}
	
	public boolean addReplicationCommandBlockToStore( CReplicationCommandBlock ReplicationCommandBlock, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = false;
		
		try {

			if ( ReplicationCommandBlock.strInstanceID.equals( strInstanceID ) ) {
			
				StringBuilder CommandBlock = new StringBuilder();

				CommandBlock.append( "<CommandBlock>\n" );
				CommandBlock.append( "<InstanceID>" + ReplicationCommandBlock.strInstanceID + "</InstanceID>\n" );
				CommandBlock.append( "<SourceDataBlockID>" + ReplicationCommandBlock.strSourceDataBlockID + "</SourceDataBlockID>\n" );
				CommandBlock.append( "<TransactionID>" + ReplicationCommandBlock.strTransactionID + "</TransactionID>\n" );
				CommandBlock.append( "<DataBlockID>" + ReplicationCommandBlock.strDataBlockID + "</DataBlockID>\n" );
				CommandBlock.append( "<CommandID>" + ReplicationCommandBlock.strCommandID + "</CommandID>\n" );
				CommandBlock.append( "<Command><![CDATA[\n" );
				CommandBlock.append( ReplicationCommandBlock.strCommand + "\n" );
				CommandBlock.append( "]]></Command>\n" );
				CommandBlock.append( "<User>" + ReplicationCommandBlock.strUser + "</User>\n" );
				CommandBlock.append( "<Database>" + ReplicationCommandBlock.strDatabase + "</Database>\n" );

				for ( Entry<String,String> Param : ReplicationCommandBlock.Params.entrySet() ) {

					CommandBlock.append( "<Param><![CDATA[\n" );
					CommandBlock.append( Param.getKey() + "\"=" + Param.getValue() + "\n" );
					CommandBlock.append( "]]></Param>\n" );

				}

				//CurrentReplicationStoreFileWriter.println( _End_Command_Block );
				CommandBlock.append( "</CommandBlock>\n" );

				int intOrigNumberLength = Long.toString( CommandBlock.length() ).length();

				String strCommandBlockLength = "<CommandBlockLength>" + Integer.toString( CommandBlock.length() ) + "</CommandBlockLength>\n";

				long lngTotal = CommandBlock.length() + strCommandBlockLength.length();

				int intLastNumberLength = Long.toString( lngTotal ).length();

				if ( intLastNumberLength > intOrigNumberLength ) {

					lngTotal += intLastNumberLength - intOrigNumberLength;

				}

				CommandBlock.insert( CommandBlock.length(), "<CommandBlockLength>" + Long.toString( lngTotal ) + "</CommandBlockLength>\n" );

				LockWriter.acquire();

				if ( ReplicationStoreFilePath.length() >= intHeaderLength && ReplicationStoreFilePath.length() + CommandBlock.length() > PlainTextDBReplicationStore.ConfigPlainTextDBReplicationStore.lngMaxSizeDataBlock ) {

					createDataBlockFileStore( false, Logger, Lang ); //Force create store file for don't exceed the max size of replication store file

				}

				CurrentReplicationStoreFileWriter.print( CommandBlock.toString() );
				CurrentReplicationStoreFileWriter.flush();

				lngLastDataBlockWrite = System.currentTimeMillis();
				
				bResult = true;

			}
			
		}
		catch ( Error Err ) {

			if ( Logger != null )
				Logger.logError( "-1020", Err.getMessage(), Err );

		}
		catch ( Exception Ex ) {

			if ( Logger != null )
				Logger.logException( "-1021", Ex.getMessage(), Ex );

		}
		finally {
			
			LockWriter.release();

		}
		
		return bResult;
		
	}
	
}
