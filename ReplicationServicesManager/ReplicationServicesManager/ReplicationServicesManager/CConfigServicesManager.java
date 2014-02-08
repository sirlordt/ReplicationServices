package ReplicationServicesManager;

import java.io.File;
import java.util.ArrayList;

import net.maindataservices.Utilities;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import CommonClasses.CAbstractConfigLoader;
import CommonClasses.CConfigRegisterService;
import CommonClasses.CLanguage;
import CommonClasses.ConstantsCommonConfigXMLTags;
import CommonClasses.ConstantsCommonClasses;
import CommonClasses.ConstantsMessagesCodes;
import ExtendedLogger.CExtendedLogger;

public class CConfigServicesManager extends CAbstractConfigLoader {

	protected static CConfigServicesManager ConfigRegisterServicesManager = null;
	
	public String strGlobalDateTimeFormat;
	public String strGlobalDateFormat;
	public String strGlobalTimeFormat;

	public int intRequestTimeout;
	public int intSocketTimeout;
	
	public String strTempDir;
	public String strServicesDir;
	public String strResponsesFormatsDir;
	public String strDefaultResponseFormat;
	public String strDefaultResponseFormatVersion;
	public String strResponseRequestMethod; //OnlyGET, OnlyPOST, Any
	
	//Built in responses formats configs
	public String strXML_DataPacket_ContentType;
	public String strXML_DataPacket_CharSet;
	
	public String strJavaXML_WebRowSet_ContentType;
	public String strJavaXML_WebRowSet_CharSet;
	
	public String strJSON_ContentType;
	public String strJSON_CharSet;
	
	public String strCSV_ContentType;
	public String strCSV_CharSet;
	public boolean bCSV_FieldQuoted;
	public String strCSV_SeparatorSymbol;
	public boolean bCSV_ShowHeaders;
	
	public ArrayList<CConfigRegisterService> ConfiguredRegisterServices;
	
	public static CConfigServicesManager getConfigRegisterServicesManager( String strRunningPath ) {
		
		if ( ConfigRegisterServicesManager == null ) {
			
			ConfigRegisterServicesManager = new CConfigServicesManager( strRunningPath );
			
		}
		
		return ConfigRegisterServicesManager;
		
	}
	
	public CConfigServicesManager( String strRunningPath ) {
		
		super( strRunningPath );
		
		//Set the order for read xml config file sections
		strFirstLevelConfigSectionsOrder.add( ConstantsCommonConfigXMLTags._System );  //1
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConstantsCommonConfigXMLTags._RegisterServices ); //2
		bFirstLevelConfigSectionsMustExists.add( true );
		strFirstLevelConfigSectionsOrder.add( ConstantsCommonConfigXMLTags._BuiltinResponsesFormats ); //3
		bFirstLevelConfigSectionsMustExists.add( true );
		
		strTempDir = strRunningPath + ConstantsCommonClasses._Temp_Dir; //"Temp/";
		strServicesDir = strRunningPath + ConstantsServicesManager._Services_Dir; //"DBServices/"; 
		strResponsesFormatsDir = strRunningPath + ConstantsCommonClasses._Responses_Formats_Dir; //"ResponsesFormats/";
		
		strGlobalDateTimeFormat = ConstantsCommonClasses._Global_Date_Time_Format;
		strGlobalDateFormat = ConstantsCommonClasses._Global_Date_Format;
		strGlobalTimeFormat = ConstantsCommonClasses._Global_Time_Format;
		
		strDefaultResponseFormat = ConstantsServicesManager._Response_Format;
		strDefaultResponseFormatVersion = ConstantsServicesManager._Response_Format_Version;

		strResponseRequestMethod = ConstantsCommonConfigXMLTags._Request_Method_ANY;
		
		strXML_DataPacket_CharSet = ConstantsCommonClasses._Chaset_XML;
		strXML_DataPacket_ContentType = ConstantsCommonClasses._Content_Type_XML;
		
		strJavaXML_WebRowSet_CharSet = ConstantsCommonClasses._Chaset_XML;
		strJavaXML_WebRowSet_ContentType = ConstantsCommonClasses._Content_Type_XML;

		strJSON_ContentType = ConstantsCommonClasses._Content_Type_JSON;
		strJSON_CharSet = ConstantsCommonClasses._Chaset_JSON;
		
		strCSV_ContentType = ConstantsCommonClasses._Content_Type_CSV;
		strCSV_CharSet = ConstantsCommonClasses._Chaset_CSV;
		bCSV_FieldQuoted = ConstantsCommonClasses._Fields_Quote_CSV;
		strCSV_SeparatorSymbol = ConstantsCommonClasses._Separator_Symbol_CSV;
		bCSV_ShowHeaders = ConstantsCommonClasses._Show_Headers_CSV;

		intRequestTimeout = ConstantsCommonClasses._Request_Timeout;
		intSocketTimeout = ConstantsCommonClasses._Socket_Timeout;

		ConfiguredRegisterServices = new ArrayList<CConfigRegisterService>();
		
	}
	
    public boolean loadConfigSectionSystem( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {

        boolean bResult = true;
		
        try {
		   
			if ( ConfigSectionNode.hasAttributes() == true ) {
		
				String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._Password, ConstantsCommonConfigXMLTags._Temp_Dir, ConstantsConfigXMLTags._ReplicationServices_Dir, ConstantsCommonConfigXMLTags._Responses_Formats_Dir, ConstantsCommonConfigXMLTags._Default_Response_Format, ConstantsCommonConfigXMLTags._Default_Response_Format_Version, ConstantsCommonConfigXMLTags._Response_Request_Method, ConstantsCommonConfigXMLTags._Request_Timeout, ConstantsCommonConfigXMLTags._Socket_Timeout };

				NamedNodeMap NodeAttributes = ConfigSectionNode.getAttributes();

		        for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {
		        	
		            Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );
		        	
		            if ( NodeAttribute != null ) {
		            	
		            	Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
		            	Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );
						
						if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Temp_Dir ) ) {

							this.strTempDir = NodeAttribute.getNodeValue();
		
					        if ( this.strTempDir != null && this.strTempDir.isEmpty() == false && new File( this.strTempDir ).isAbsolute() == false ) {

					        	this.strTempDir = this.strRunningPath + this.strTempDir;
						        	
						    }

					        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strTempDir", this.strTempDir ) );
				        
					        if ( Utilities.checkDir( this.strTempDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsConfigXMLTags._ReplicationServices_Dir ) ) {

							this.strServicesDir = NodeAttribute.getNodeValue();
		
					        if ( this.strServicesDir != null && this.strServicesDir.isEmpty() == false && new File( this.strServicesDir ).isAbsolute() == false ) {

					        	this.strServicesDir = this.strRunningPath + this.strServicesDir;
						        	
						    }

					        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strDBServicesDir", this.strServicesDir ) );
				        
					        if ( Utilities.checkDir( this.strServicesDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
						
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Responses_Formats_Dir ) ) {

							this.strResponsesFormatsDir = NodeAttribute.getNodeValue();
		            		  
					        if ( this.strResponsesFormatsDir != null && this.strResponsesFormatsDir.isEmpty() == false && new File( this.strResponsesFormatsDir ).isAbsolute() == false ) {
	                        
					        	this.strResponsesFormatsDir = this.strRunningPath + this.strResponsesFormatsDir;
						        	
						    }

					        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strResponsesFormatsDir", this.strResponsesFormatsDir ) );
						
					        if ( Utilities.checkDir( this.strResponsesFormatsDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Default_Response_Format ) ) {
						
					        if ( NodeAttribute.getNodeValue() != null && NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	this.strDefaultResponseFormat = NodeAttribute.getNodeValue();
						    	
						        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strDefaultResponseFormat", this.strDefaultResponseFormat ) );

						    }
					        else {
					        	
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Default_Response_Format, this.strDefaultResponseFormat ) );
					        	
					        }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Default_Response_Format_Version ) ) {
							
					        if ( NodeAttribute.getNodeValue() != null && NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	this.strDefaultResponseFormatVersion = NodeAttribute.getNodeValue();
						    	
						        Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strDefaultResponseFormatVersion", this.strDefaultResponseFormatVersion ) );

						    }
					        else {
					        	
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Default_Response_Format_Version, this.strDefaultResponseFormatVersion ) );
					        	
					        }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Response_Request_Method ) ) {
							
					        if ( NodeAttribute.getNodeValue() != null && NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	if ( NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConstantsCommonConfigXMLTags._Request_Method_ANY.toLowerCase() ) ) {

					        		this.strResponseRequestMethod = ConstantsCommonConfigXMLTags._Request_Method_ANY;
							        
					        		Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strResponseRequestMethod", this.strResponseRequestMethod ) );
					        
					        
					        	}
					        	else if ( NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConstantsCommonConfigXMLTags._Request_Method_OnlyGET.toLowerCase() ) ) { 

					        		this.strResponseRequestMethod = ConstantsCommonConfigXMLTags._Request_Method_OnlyGET;
							        
					        		Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strResponseRequestMethod", this.strResponseRequestMethod ) );

					        	}	
					        	else if ( NodeAttribute.getNodeValue().trim().toLowerCase().equals( ConstantsCommonConfigXMLTags._Request_Method_OnlyPOST.toLowerCase() ) ) {    

					        	   this.strResponseRequestMethod = ConstantsCommonConfigXMLTags._Request_Method_OnlyPOST;
						    	
						           Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strResponseRequestMethod", this.strResponseRequestMethod ) );

					        	}
					        	else {
					        		
						           Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] must be only one of the next values: [%s,%s,%s]", ConstantsCommonConfigXMLTags._Response_Request_Method, ConstantsCommonConfigXMLTags._Request_Method_ANY, ConstantsCommonConfigXMLTags._Request_Method_OnlyGET, ConstantsCommonConfigXMLTags._Request_Method_OnlyPOST ) );
						           Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Response_Request_Method, NodeAttribute.getNodeValue(), this.strResponseRequestMethod ) );
					        		
					        	}
						    	
						    }
					        else {
					        	
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Response_Request_Method, this.strResponseRequestMethod ) );
					        	
					        }
					        
						}
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Request_Timeout ) ) {

					        if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
					        
					        	int intTmpRequestTimeout = net.maindataservices.Utilities.strToInteger( NodeAttribute.getNodeValue().trim() );
					        	
					        	if ( intTmpRequestTimeout >= ConstantsCommonClasses._Minimal_Request_Timeout && intTmpRequestTimeout <= ConstantsCommonClasses._Maximal_Request_Timeout ) {
					        		
					        		intRequestTimeout = intTmpRequestTimeout;
					        		
					        	}
					        	else {
					        		
									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", ConstantsCommonConfigXMLTags._Request_Timeout, Integer.toString( intTmpRequestTimeout ), Integer.toString( ConstantsCommonClasses._Minimal_Request_Timeout ), Integer.toString( ConstantsCommonClasses._Maximal_Request_Timeout ) ) );
									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Request_Timeout, Integer.toString( intTmpRequestTimeout ), Integer.toString( ConstantsCommonClasses._Request_Timeout ) ) );
									
					        	}
					        	
					        }
					        else {
					        	
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Request_Timeout, Integer.toString( this.intRequestTimeout ) ) );
					        	
					        }
							
						}	
						else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Socket_Timeout ) ) {

					        if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

					        	int intTmpSocketTimeout = net.maindataservices.Utilities.strToInteger( NodeAttribute.getNodeValue().trim() );
					        	
					        	if ( intTmpSocketTimeout >= ConstantsCommonClasses._Minimal_Socket_Timeout && intTmpSocketTimeout <= ConstantsCommonClasses._Maximal_Socket_Timeout ) {
					        		
					        		intSocketTimeout = intTmpSocketTimeout;
					        		
					        	}
					        	else {
					        		
									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", ConstantsCommonConfigXMLTags._Socket_Timeout, Integer.toString( intTmpSocketTimeout ), Integer.toString( ConstantsCommonClasses._Minimal_Socket_Timeout ), Integer.toString( ConstantsCommonClasses._Maximal_Socket_Timeout ) ) );
									Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Socket_Timeout, Integer.toString( intTmpSocketTimeout ), Integer.toString( ConstantsCommonClasses._Socket_Timeout ) ) );
									
					        	}
						        
					        }
					        else {
					        	
						        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Socket_Timeout, Integer.toString( this.intSocketTimeout ) ) );
					        	
					        }
					        
						}	
						
		            }
		            else {

		            	if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsConfigXMLTags._ReplicationServices_Dir ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsConfigXMLTags._ReplicationServices_Dir, this.strServicesDir ) );

					        if ( Utilities.checkDir( this.strServicesDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Temp_Dir ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Temp_Dir, this.strTempDir ) );

					        if ( Utilities.checkDir( this.strTempDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Responses_Formats_Dir ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Responses_Formats_Dir, this.strResponsesFormatsDir ) );

					        if ( Utilities.checkDir( this.strResponsesFormatsDir, Logger, Lang ) == false ) {
						    	
					        	bResult = false;
					        	
					        	break;
						    	
						    }
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Default_Response_Format ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Default_Response_Format, this.strDefaultResponseFormat ) );
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Default_Response_Format_Version ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Default_Response_Format_Version, this.strDefaultResponseFormatVersion ) );
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Response_Request_Method ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Response_Request_Method, this.strResponseRequestMethod ) );
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Request_Timeout ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Request_Timeout, Integer.toString( this.intRequestTimeout ) ) );
		            		
		            	}
		            	else if ( strAttributesOrder[ intAttributesIndex ].equals( ConstantsCommonConfigXMLTags._Socket_Timeout ) ) {
		            		
					        Logger.logWarning( "-1", Lang.translate( "The [%s] attribute not found, using the default value [%s]", ConstantsCommonConfigXMLTags._Socket_Timeout, Integer.toString( this.intSocketTimeout ) ) );
		            		
		            	}
		            	
		            }
		            
		        }
			
			}
		
		}
		catch ( Exception Ex ) {
			
        	bResult = false;
        	
			Logger.logException( "-1010", Ex.getMessage(), Ex );
			
		}
        
        return bResult;

	}
	
	public boolean loadConfigSectionRegisterServices( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {
		
        boolean bResult = true;
        
        try {
        	
			String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._Password, ConstantsCommonConfigXMLTags._URL, ConstantsCommonConfigXMLTags._Proxy_IP, ConstantsCommonConfigXMLTags._Proxy_Port, ConstantsCommonConfigXMLTags._Proxy_User, ConstantsCommonConfigXMLTags._Proxy_Password, ConstantsCommonConfigXMLTags._Interval, ConstantsCommonConfigXMLTags._Weight, ConstantsCommonConfigXMLTags._ReportLoad, ConstantsCommonConfigXMLTags._ReportIPType };

			NodeList ConfigRegisterServiceList = ConfigSectionNode.getChildNodes();
	          
	        if ( ConfigRegisterServiceList.getLength() > 0 ) {
			
	            for ( int intConfigRegisterIndex = 0; intConfigRegisterIndex < ConfigRegisterServiceList.getLength(); intConfigRegisterIndex++ ) {
	                
	            	Node ConfigRegisterServicesNode = ConfigRegisterServiceList.item( intConfigRegisterIndex );
	                 
	    			Logger.logMessage( "1", Lang.translate( "Reading XML built in response format: [%s]", ConfigRegisterServicesNode.getNodeName() ) );        
	                 
	    			if ( ConfigRegisterServicesNode.getNodeName().equals( ConstantsCommonConfigXMLTags._Register ) == true ) {

	    				String strPassword = "";
						String strURL = "";
						String strProxyIP = "";
					    int intProxyPort = 0;
					    String strProxyUser = "";
					    String strProxyPassword = "";
						int intInterval = 0;
						int intWeight = 0;
						boolean bReportLoad = false;
						int intReportIPType = 0; //all

	    				if ( ConfigRegisterServicesNode.hasAttributes() == true ) {

							NamedNodeMap NodeAttributes = ConfigRegisterServicesNode.getAttributes();

							for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {

								Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );

								if ( NodeAttribute != null  ) {

									Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
									Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );

									if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Password ) ) {
										
										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strPassword = NodeAttribute.getNodeValue().trim();
											
										}
										else {
											
									    	Logger.logError( "-1002", Lang.translate( "The [%s] attribute cannot empty string", ConstantsCommonConfigXMLTags._Password ) );
											break;
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._URL ) ) {
										
										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strURL = NodeAttribute.getNodeValue().trim();
											
										}
										else {
											
									    	Logger.logError( "-1003", Lang.translate( "The [%s] attribute cannot empty string", ConstantsCommonConfigXMLTags._URL ) );
											break;
											
										}
										
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Proxy_IP ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

											if ( Utilities.isValidIP( NodeAttribute.getNodeValue() ) == true ) {

												strProxyIP = NodeAttribute.getNodeValue();

											}
											else {

												Logger.logError( "-1004", Lang.translate( "The [%s] attribute value [%s] is not valid ip address", ConstantsCommonConfigXMLTags._Proxy_IP, NodeAttribute.getNodeValue() ) );
												break; //Stop parse more attributes

											}

										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty string", ConstantsCommonConfigXMLTags._Proxy_IP ) );
											
										}
									    
									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Proxy_Port ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {

											int intTmpPort = Utilities.strToInteger( NodeAttribute.getNodeValue().trim(), Logger );

											if ( intTmpPort >= ConstantsCommonClasses._Min_Port_Number && intTmpPort <= ConstantsCommonClasses._Max_Port_Number ) {

												intProxyPort = intTmpPort;

											}
											else {

												Logger.logError( "-1005", Lang.translate( "The [%s] attribute value [%s] is out of range. Must be integer value in the next inclusive range, minimum [%s] and maximum [%s]", ConstantsCommonConfigXMLTags._Proxy_Port, NodeAttribute.getNodeValue(), Integer.toString( ConstantsCommonClasses._Min_Port_Number ), Integer.toString( ConstantsCommonClasses._Max_Port_Number ) ) );
												break; //Stop parse more attributes

											}

										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty string", ConstantsCommonConfigXMLTags._Proxy_Port ) );
											
										}

									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Proxy_User ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strProxyUser = NodeAttribute.getNodeValue().trim();
											
										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty string", ConstantsCommonConfigXMLTags._Proxy_User ) );
											
										}

									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Proxy_Password ) ) {

										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strProxyPassword = NodeAttribute.getNodeValue().trim();
											
										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is empty string", ConstantsCommonConfigXMLTags._Proxy_Password ) );
											
										}

									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Interval ) ) {

										intInterval = Utilities.strToInteger( NodeAttribute.getNodeValue().trim(), Logger );
										
										if ( intInterval < ConstantsCommonClasses._Minimal_Register_Manager_Frecuency ) {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Interval, Integer.toString( intInterval ), Integer.toString( ConstantsCommonClasses._Register_Manager_Frecuency ) ) );
											
											intInterval = ConstantsCommonClasses._Register_Manager_Frecuency;
											
										}

									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Weight ) ) {

										intWeight = Utilities.strToInteger( NodeAttribute.getNodeValue().trim(), Logger );
										
										if ( intWeight < 1 ) {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute value [%s] is invalid, using the default value [%s]", ConstantsCommonConfigXMLTags._Weight, Integer.toString( intWeight ), "1" ) );

											intWeight = 1;
											
										}

									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._ReportLoad ) ) {

										bReportLoad = NodeAttribute.getNodeValue().trim().equals( "true" );

									}
									else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._ReportIPType ) ) {

										if ( NodeAttribute.getNodeValue().trim().equals( ConstantsCommonConfigXMLTags._IPV4 ) ) {
											
											intReportIPType = 1; //IPV4
											
										}
										else if ( NodeAttribute.getNodeValue().trim().equals( ConstantsCommonConfigXMLTags._IPV6 ) ) {
											
											intReportIPType = 2; //IPV6
											
										}
										else {

											intReportIPType = 0; //IPAll
											
										}

									}

								}
								else { //The attribute is obligatory 
									
							    	Logger.logError( "-1001", Lang.translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", strAttributesOrder[ intAttributesIndex ], ConstantsCommonConfigXMLTags._Register, Integer.toString( intConfigRegisterIndex ) ) );
							    	break;
							    	
								}

							}
							
						}

	    				if ( strPassword.isEmpty() == false && strURL.isEmpty() == false && intInterval >= ConstantsCommonClasses._Minimal_Register_Manager_Frecuency && intWeight >= 1 ) {
							
	    					CConfigRegisterService ConfigRegisterService = new CConfigRegisterService();
	    					
	    					ConfigRegisterService.strPassword = strPassword;
	    					ConfigRegisterService.strURL = strURL;

	    					if ( strProxyIP.isEmpty() == false ) {
	    					
	    						ConfigRegisterService.ConfigProxy.strProxyIP = strProxyIP;
	    						ConfigRegisterService.ConfigProxy.intProxyPort = intProxyPort;
	    						
	    						if ( ConfigRegisterService.ConfigProxy.strProxyUser.isEmpty() == false ) {
	    							
	    							ConfigRegisterService.ConfigProxy.strProxyUser = strProxyUser;
	    							ConfigRegisterService.ConfigProxy.strProxyPassword = strProxyPassword;
	    							
	    						}
	    						
	    					}
							
	    					ConfigRegisterService.intInterval = intInterval;
	    					ConfigRegisterService.intWeight = intWeight;
	    					ConfigRegisterService.bReportLoad = bReportLoad;
	    					ConfigRegisterService.intReportIPType = intReportIPType;
	    					
	    					ConfiguredRegisterServices.add( ConfigRegisterService );
	    					
						}

					}
	            
	            }

	        } 
        	
		}
		catch ( Exception Ex ) {
			
        	bResult = false;
        	
			Logger.logException( "-1010", Ex.getMessage(), Ex );
			
		}
        
        return bResult;

	}
    
	public boolean loadConfigSectionBuiltinResponsesFormats( Node ConfigSectionNode, CLanguage Lang, CExtendedLogger Logger ) {
		
        boolean bResult = true;
        
        try {
        	
			String strAttributesOrder[] = { ConstantsCommonConfigXMLTags._Name, ConstantsCommonConfigXMLTags._Char_Set, ConstantsCommonConfigXMLTags._Content_Type, ConstantsCommonConfigXMLTags._Fields_Quote, ConstantsCommonConfigXMLTags._Separator_Symbol, ConstantsCommonConfigXMLTags._Show_Headers };

			NodeList ConfigBuiltinResponsesList = ConfigSectionNode.getChildNodes();
	          
	        if ( ConfigBuiltinResponsesList.getLength() > 0 ) {
			
	            for ( int intConfigBuiltinResponseFormatIndex = 0; intConfigBuiltinResponseFormatIndex < ConfigBuiltinResponsesList.getLength(); intConfigBuiltinResponseFormatIndex++ ) {
	                
	            	Node ConfigBuiltinResponseFormatNode = ConfigBuiltinResponsesList.item( intConfigBuiltinResponseFormatIndex );
	                 
	    			Logger.logMessage( "1", Lang.translate( "Reading XML built in response format: [%s]", ConfigBuiltinResponseFormatNode.getNodeName() ) );        
	                 
				    String strBuitinResponseName = "";
	    			
	    			if ( ConfigBuiltinResponseFormatNode.getNodeName().equals( ConstantsCommonConfigXMLTags._BuiltinResponseFormat ) == true ) {

						if ( ConfigBuiltinResponseFormatNode.hasAttributes() == true ) {

							NamedNodeMap NodeAttributes = ConfigBuiltinResponseFormatNode.getAttributes();

							for ( int intAttributesIndex = 0; intAttributesIndex < strAttributesOrder.length; intAttributesIndex++ ) {

								Node NodeAttribute = NodeAttributes.getNamedItem( strAttributesOrder[ intAttributesIndex ] );

								if ( NodeAttribute != null  ) {

									Logger.logMessage( "1", Lang.translate( "Node attribute name: [%s]", NodeAttribute.getNodeName() ) );
									Logger.logMessage( "1", Lang.translate( "Node attribute value: [%s]", NodeAttribute.getNodeValue() ) );

									if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Name ) ) {
										
										if ( NodeAttribute.getNodeValue().trim().isEmpty() == false ) {
											
											strBuitinResponseName = NodeAttribute.getNodeValue().trim().toUpperCase();
											
										}
										else {
											
									    	Logger.logError( "-1002", Lang.translate( "The [%s] attribute cannot empty string, for the node [%s] at relative index [%s]", ConstantsCommonConfigXMLTags._Name, ConstantsCommonConfigXMLTags._BuiltinResponseFormat, Integer.toString( intConfigBuiltinResponseFormatIndex ) ) );
											
										}
										
									}
									else if ( strBuitinResponseName.equals( ConstantsCommonConfigXMLTags._ResponseFormat_XML_DATAPACKET ) ) {

										if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Char_Set ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strXML_DataPacket_CharSet = NodeAttribute.getNodeValue().toUpperCase();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strXML_DataPacket_CharSet", this.strXML_DataPacket_CharSet ) );

											}
											else {

												Logger.logError( "-1003", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Char_Set, ConstantsCommonClasses._Chaset_XML ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Content_Type ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strXML_DataPacket_ContentType = NodeAttribute.getNodeValue();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strXML_DataPacket_ContentType", this.strXML_DataPacket_ContentType ) );

											}
											else {

												Logger.logError( "-1004", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Content_Type, ConstantsCommonClasses._Content_Type_XML ) );
												break; //stop the parse another attributes

											}

										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is unknown for the node [%s]", NodeAttribute.getNodeName(), ConfigBuiltinResponseFormatNode.getNodeName() ) );
											
										}

									}
									else if ( strBuitinResponseName.equals( ConstantsCommonConfigXMLTags._ResponseFormat_JAVA_XML_WEBROWSET ) ) {

										if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Char_Set ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strJavaXML_WebRowSet_CharSet = NodeAttribute.getNodeValue().toUpperCase();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strJavaXML_WebRowSet_CharSet", this.strJavaXML_WebRowSet_CharSet ) );

											}
											else {

												Logger.logError( "-1005", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Char_Set, ConstantsCommonClasses._Chaset_XML ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Content_Type ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strJavaXML_WebRowSet_ContentType = NodeAttribute.getNodeValue();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strJavaXML_WebRowSet_ContentType", this.strJavaXML_WebRowSet_ContentType ) );

											}
											else {

												Logger.logError( "-1006", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Content_Type, ConstantsCommonClasses._Content_Type_XML ) );
												break; //stop the parse another attributes

											}

										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is unknown for the node [%s]", NodeAttribute.getNodeName(), ConfigBuiltinResponseFormatNode.getNodeName() ) );
											
										}

									}
									else if ( strBuitinResponseName.equals( ConstantsCommonConfigXMLTags._ResponseFormat_JSON ) ) {

										if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Char_Set ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strJSON_CharSet = NodeAttribute.getNodeValue().toUpperCase();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strJSON_CharSet", this.strJSON_CharSet ) );

											}
											else {

												Logger.logError( "-1007", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Char_Set, ConstantsCommonClasses._Chaset_XML ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Content_Type ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strJSON_ContentType = NodeAttribute.getNodeValue();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strJSON_ContentType", this.strJSON_ContentType ) );

											}
											else {

												Logger.logError( "-1008", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Content_Type, ConstantsCommonClasses._Content_Type_XML ) );
												break; //stop the parse another attributes

											}

										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is unknown for the node [%s]", NodeAttribute.getNodeName(), ConfigBuiltinResponseFormatNode.getNodeName() ) );
											
										}

									}
									else if ( strBuitinResponseName.equals( ConstantsCommonConfigXMLTags._ResponseFormat_CSV ) ) {

										if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Char_Set ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strCSV_CharSet = NodeAttribute.getNodeValue().toUpperCase();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strCSV_CharSet", this.strCSV_CharSet ) );

											}
											else {

												Logger.logError( "-1009", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Char_Set, ConstantsCommonClasses._Chaset_XML ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Content_Type ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strCSV_ContentType = NodeAttribute.getNodeValue();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strCSV_ContentType", this.strCSV_ContentType ) );

											}
											else {

												Logger.logError( "-1010", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Content_Type, ConstantsCommonClasses._Content_Type_XML ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Fields_Quote ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.bCSV_FieldQuoted = NodeAttribute.getNodeValue().equals( "true" );
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "bCSV_FieldQuoted", Boolean.toString( this.bCSV_FieldQuoted ) ) );

											}
											else {

												Logger.logError( "-1010", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Fields_Quote, Boolean.toString( ConstantsCommonClasses._Fields_Quote_CSV ) ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Separator_Symbol ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.strCSV_SeparatorSymbol = NodeAttribute.getNodeValue();
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "strCSV_SeparatorSymbol", this.strCSV_SeparatorSymbol ) );

											}
											else {

												Logger.logError( "-1010", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Separator_Symbol, ConstantsCommonClasses._Separator_Symbol_CSV ) );
												break; //stop the parse another attributes

											}

										}
										else if ( NodeAttribute.getNodeName().equals( ConstantsCommonConfigXMLTags._Show_Headers ) ) {

											if ( NodeAttribute.getNodeValue().isEmpty() == false ) {

												this.bCSV_ShowHeaders = NodeAttribute.getNodeValue().equals( "true" );
												Logger.logMessage( "1", Lang.translate( "Runtime config value [%s] changed to: [%s]", "bCSV_ShowHeaders", Boolean.toString( this.bCSV_ShowHeaders ) ) );

											}
											else {

												Logger.logError( "-1010", Lang.translate( "The [%s] attribute cannot empty string, using the default value [%s]", ConstantsCommonConfigXMLTags._Show_Headers, Boolean.toString( ConstantsCommonClasses._Show_Headers_CSV ) ) );
												break; //stop the parse another attributes

											}

										}
										else {
											
											Logger.logWarning( "-1", Lang.translate( "The [%s] attribute is unknown for the node [%s]", NodeAttribute.getNodeName(), ConfigBuiltinResponseFormatNode.getNodeName() ) );
											
										}

									}

								}
								else if ( intAttributesIndex == 0 ) { //Only the name attribute is obligatory 
									
							    	Logger.logError( "-1001", Lang.translate( "The [%s] attribute not found, for the node [%s] at relative index [%s], ignoring the node", ConstantsCommonConfigXMLTags._Name, ConstantsCommonConfigXMLTags._BuiltinResponseFormat, Integer.toString( intConfigBuiltinResponseFormatIndex ) ) );
							    	break;
							    	
								}

							}

						}

					}
	            
	            }

	        } 
        	
		}
		catch ( Exception Ex ) {
			
        	bResult = false;
        	
			Logger.logException( "-1010", Ex.getMessage(), Ex );
			
		}
        
        return bResult;

	}
	
	@Override
	public boolean loadConfigSection( Node ConfigSectionNode, CExtendedLogger Logger, CLanguage Lang ) {

		boolean bResult = true;

		Logger.logMessage( "1", Lang.translate( "Reading XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
        
		if ( ConfigSectionNode.getNodeName().equals(  CommonClasses.ConstantsCommonConfigXMLTags._System ) == true ) {
           
			if ( this.loadConfigSectionSystem( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.logError( "-1001", Lang.translate( "Failed to load config from XML node section: [%s] ", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	 
        }
        else if ( ConfigSectionNode.getNodeName().equals( ConstantsCommonConfigXMLTags._RegisterServices ) == true ) {

			if ( this.loadConfigSectionRegisterServices( ConfigSectionNode, Lang, Logger ) == false ) {
				
    			Logger.logError( "-1002", Lang.translate( "Failed to load config from XML node section: [%s] ", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
				
			} 
        	
        }	
        else if ( ConfigSectionNode.getNodeName().equals( ConstantsCommonConfigXMLTags._BuiltinResponsesFormats ) == true ) {

        	if ( this.loadConfigSectionBuiltinResponsesFormats( ConfigSectionNode, Lang, Logger ) == false ) {
        		
    			Logger.logError( "-1003", Lang.translate( "Failed to load config from XML node section: [%s]", ConfigSectionNode.getNodeName() ) );        
				
    			bResult = false;
        		
        	}
             
        }
		
		return bResult;
		
	}

	@Override
	public Object sendMessage( String strMessageName, Object MessageData ) {

    	try {

    		if ( strMessageName.equals( ConstantsMessagesCodes._XML_DataPacket_CharSet ) )
    			return this.strXML_DataPacket_CharSet;
    		else if ( strMessageName.equals( ConstantsMessagesCodes._XML_DataPacket_ContentType ) )
    			return this.strXML_DataPacket_ContentType;
    		else if ( strMessageName.equals( ConstantsMessagesCodes._JavaXML_WebRowSet_CharSet ) )
    			return this.strJavaXML_WebRowSet_CharSet;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._JavaXML_WebRowSet_ContentType ) )
    			return this.strJavaXML_WebRowSet_ContentType;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._JSON_CharSet ) )
    			return this.strJSON_CharSet;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._JSON_ContentType ) )
    			return this.strJSON_ContentType;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._CSV_CharSet ) )
    			return this.strCSV_CharSet;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._CSV_ContentType ) )
    			return this.strCSV_ContentType;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._CSV_FieldsQuote ) )
    			return Boolean.toString( this.bCSV_FieldQuoted );  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._CSV_SeparatorSymbol ) )
    			return this.strCSV_SeparatorSymbol;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._CSV_ShowHeaders ) )
    			return Boolean.toString( this.bCSV_ShowHeaders );  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._Global_DateTime_Format ) )
    			return this.strGlobalDateTimeFormat;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._Global_Date_Format ) )
    			return this.strGlobalDateFormat;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._Global_Time_Format ) )
    			return this.strGlobalTimeFormat;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._Temp_Dir ) )
    			return this.strTempDir;  
    		else if ( strMessageName.equals( ConstantsMessagesCodes._Security_Manager_Name ) )
    			return ConstantsServicesManager._Security_Manager_Name;
    		else	
    			return "";

    	}
    	catch ( Exception Ex ) {

    		return "";

    	}
		
	}
	
}
