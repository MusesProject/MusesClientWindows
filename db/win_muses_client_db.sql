CREATE SCHEMA IF NOT EXISTS muses_client;
USE muses_client;
DROP TABLE IF EXISTS action;
CREATE TABLE action ( id INTEGER PRIMARY KEY,description VARCHAR(45),action_type VARCHAR(45),timestamp TIMESTAMP NOT NULL);
INSERT INTO action VALUES(1,'security_property_changed',NULL,0);
DROP TABLE IF EXISTS action_property;
CREATE TABLE action_property ( id INTEGER PRIMARY KEY,action_id INT NOT NULL,key VARCHAR(45) NOT NULL,value VARCHAR(500) NOT NULL);
DROP TABLE IF EXISTS android_metadata;
CREATE TABLE android_metadata (locale TEXT);
INSERT INTO android_metadata VALUES('en_GB');
DROP TABLE IF EXISTS configuration;
CREATE TABLE configuration	 ( id INTEGER PRIMARY KEY,server_ip VARCHAR(45) NOT NULL DEFAULT '192.168.44.101',server_port VARCHAR(45) NOT NULL DEFAULT '8443',server_context_path VARCHAR(45) NOT NULL DEFAULT '/server',server_servlet_path VARCHAR(45) NOT NULL DEFAULT '/commain',server_certificate VARCHAR(4500) NOT NULL,client_certificate VARCHAR(4500) NOT NULL,timeout INTEGER NOT NULL DEFAULT 5000,poll_timeout INTEGER NOT NULL DEFAULT 10000,sleep_poll_timeout INTEGER NOT NULL DEFAULT 60000,polling_enabled INTEGER NOT NULL DEFAULT 1,login_attempts INTEGER NOT NULL DEFAULT 5,silent_mode INTEGER NOT NULL DEFAULT 0);
INSERT INTO configuration VALUES(1,'sweoffice.mooo.com','8443','/server','/commain','-----BEGIN CERTIFICATE-----
MIIDfjCCAmYCCQDldBeawF7c3DANBgkqhkiG9w0BAQUFADCBgDELMAkGA1UEBhMC
U1YxFjAUBgNVBAgMDVN0b2NraG9sbSBMQU4xEjAQBgNVBAcMCVN0b2NraG9sbTEM
MAoGA1UECgwDU1dFMQswCQYDVQQLDAJJVDEqMCgGCSqGSIb3DQEJARYbaW5mb0Bz
d2VkZW5jb25uZWN0aXZpdHkuY29tMB4XDTE1MDExOTE5NDQyOFoXDTE2MDExOTE5
NDQyOFowgYAxCzAJBgNVBAYTAlNWMRYwFAYDVQQIDA1TdG9ja2hvbG0gTEFOMRIw
EAYDVQQHDAlTdG9ja2hvbG0xDDAKBgNVBAoMA1NXRTELMAkGA1UECwwCSVQxKjAo
BgkqhkiG9w0BCQEWG2luZm9Ac3dlZGVuY29ubmVjdGl2aXR5LmNvbTCCASIwDQYJ
KoZIhvcNAQEBBQADggEPADCCAQoCggEBAKyJZ0pIUUEF85XGuG6Y4z4MebWCneki
6mjpKCrCYzy+rTvmRK0HxMuVbFZBqDEVzO5Lya8XmteABOOnazrL9es4xF+1Cml7
D4UtB7YYsh46rH32KTGUbIxF9SzH999pvuFrfwjA4KD3Hpud8lVmxX+OVGcQMLI9
VkmoFHxVE2sDoCVyFpfjgcMgRYJD5/rHAjPUbc0jeSy49IMvpjW40cS8WtfsSNlH
JXHOh2DRAS7nOOEIMUPhTgzNgub7kX6N5CZB4vS5dJ2GVjCNIvjS7I68gnueciOT
QLK7/0MQU2EWfZiUccaVgiApoKLYm9YDlcknxcLV7t/AUSUxQlkc57ECAwEAATAN
BgkqhkiG9w0BAQUFAAOCAQEAisSeoCz6ddheXQamS8UucIqGVJZjrQXAfsmidCIR
57OAzCmjItVg9ju/9/Qj3rava+b0vbutvQjERjU/VzUV68TWgWyZd1eaVGLdCLU2
amaPxNi8y63tZN8Y7LvKI61splCpOG1t51TehkyNnbfNbkCiUG2mYVsdqI9DnO4m
wFbUcaH4gOLgHZVfSuGNLwyopNnOjcH5O7u79i+sB5fpHTDPDyS2S4ALDYBqXsfB
2e6ZTrKLaLFGgzBOu1x1xi5OgU10n6j+zjxDpfdco8YmpMC2RZWLbFNmZwodrKSy
e6UcAFMoBAAuaLg9MaOOE6ikXzGfM1wFaPksO6kfd3cLcw==
-----END CERTIFICATE-----
','',5000,60000,60000,1,5,0);
INSERT INTO configuration VALUES(2,'sweoffice.mooo.com','8443','/server','/commain','-----BEGIN CERTIFICATE-----
MIIDfjCCAmYCCQDldBeawF7c3DANBgkqhkiG9w0BAQUFADCBgDELMAkGA1UEBhMC
U1YxFjAUBgNVBAgMDVN0b2NraG9sbSBMQU4xEjAQBgNVBAcMCVN0b2NraG9sbTEM
MAoGA1UECgwDU1dFMQswCQYDVQQLDAJJVDEqMCgGCSqGSIb3DQEJARYbaW5mb0Bz
d2VkZW5jb25uZWN0aXZpdHkuY29tMB4XDTE1MDExOTE5NDQyOFoXDTE2MDExOTE5
NDQyOFowgYAxCzAJBgNVBAYTAlNWMRYwFAYDVQQIDA1TdG9ja2hvbG0gTEFOMRIw
EAYDVQQHDAlTdG9ja2hvbG0xDDAKBgNVBAoMA1NXRTELMAkGA1UECwwCSVQxKjAo
BgkqhkiG9w0BCQEWG2luZm9Ac3dlZGVuY29ubmVjdGl2aXR5LmNvbTCCASIwDQYJ
KoZIhvcNAQEBBQADggEPADCCAQoCggEBAKyJZ0pIUUEF85XGuG6Y4z4MebWCneki
6mjpKCrCYzy+rTvmRK0HxMuVbFZBqDEVzO5Lya8XmteABOOnazrL9es4xF+1Cml7
D4UtB7YYsh46rH32KTGUbIxF9SzH999pvuFrfwjA4KD3Hpud8lVmxX+OVGcQMLI9
VkmoFHxVE2sDoCVyFpfjgcMgRYJD5/rHAjPUbc0jeSy49IMvpjW40cS8WtfsSNlH
JXHOh2DRAS7nOOEIMUPhTgzNgub7kX6N5CZB4vS5dJ2GVjCNIvjS7I68gnueciOT
QLK7/0MQU2EWfZiUccaVgiApoKLYm9YDlcknxcLV7t/AUSUxQlkc57ECAwEAATAN
BgkqhkiG9w0BAQUFAAOCAQEAisSeoCz6ddheXQamS8UucIqGVJZjrQXAfsmidCIR
57OAzCmjItVg9ju/9/Qj3rava+b0vbutvQjERjU/VzUV68TWgWyZd1eaVGLdCLU2
amaPxNi8y63tZN8Y7LvKI61splCpOG1t51TehkyNnbfNbkCiUG2mYVsdqI9DnO4m
wFbUcaH4gOLgHZVfSuGNLwyopNnOjcH5O7u79i+sB5fpHTDPDyS2S4ALDYBqXsfB
2e6ZTrKLaLFGgzBOu1x1xi5OgU10n6j+zjxDpfdco8YmpMC2RZWLbFNmZwodrKSy
e6UcAFMoBAAuaLg9MaOOE6ikXzGfM1wFaPksO6kfd3cLcw==
-----END CERTIFICATE-----
','',5000,10000,60000,1,5,0);
DROP TABLE IF EXISTS contextevent;
CREATE TABLE contextevent	 ( id INTEGER PRIMARY KEY,action_id INT NOT NULL,type VARCHAR(45) NOT NULL,timestamp TIMESTAMP NOT NULL);
DROP TABLE IF EXISTS cookie_store;
CREATE TABLE cookie_store ( id INTEGER PRIMARY KEY,name VARCHAR(45) NOT NULL,domain VARCHAR(45) NOT NULL,value VARCHAR(45) NOT NULL,path VARCHAR(45) NOT NULL,version VARCHAR(45) NOT NULL,expiry VARCHAR(45) NOT NULL);
INSERT INTO cookie_store VALUES(1,'JSESSIONID','sweoffice.mooo.com','8530BB017C647D7B2CFD2B3E1AAC7BCC','/server/','0','Wed Jun 17 13:05:06 CEST 2015');
DROP TABLE IF EXISTS decision;
CREATE TABLE decision ( id INTEGER PRIMARY KEY,name VARCHAR(45) NOT NULL,decision_id VARCHAR(45),solving_risktreatment INT,condition VARCHAR(45),modification TIMESTAMP NOT NULL);
INSERT INTO decision VALUES(1,'deny','650',8,'{accessibilityEnabled:false}','09-08-2012');
DROP TABLE IF EXISTS decisiontable;
CREATE TABLE decisiontable ( id INTEGER PRIMARY KEY,action_id INT NOT NULL,resource_id INT NOT NULL,decision_id INT NOT NULL,subject_id INT NOT NULL,riskcommunication_id INT NOT NULL,modification TIMESTAMP NOT NULL);
INSERT INTO decisiontable VALUES(1,1,1,1,0,1,0);
DROP TABLE IF EXISTS offline_action;
CREATE TABLE offline_action ( id INTEGER PRIMARY KEY,description VARCHAR(45),action_type VARCHAR(45),timestamp TIMESTAMP NOT NULL);
DROP TABLE IF EXISTS offline_action_property;
CREATE TABLE offline_action_property ( id INTEGER PRIMARY KEY,action_id INT NOT NULL,key VARCHAR(45) NOT NULL,value VARCHAR(500) NOT NULL);
DROP TABLE IF EXISTS property;
CREATE TABLE property	 ( id INTEGER PRIMARY KEY,contextevent_id INT NOT NULL,key VARCHAR(45) NOT NULL,value VARCHAR(45) NOT NULL);
DROP TABLE IF EXISTS required_apps;
CREATE TABLE required_apps ( id INTEGER PRIMARY KEY,name VARCHAR(45) NOT NULL,version VARCHAR(45) NOT NULL,unique_name VARCHAR(45) NOT NULL);
DROP TABLE IF EXISTS resource;
CREATE TABLE resource ( id INTEGER PRIMARY KEY,description VARCHAR(45) NOT NULL,path VARCHAR(45) NOT NULL,condition VARCHAR(200),resourcetype INT NOT NULL,name VARCHAR(45) NOT NULL,severity VARCHAR(45) NOT NULL,type VARCHAR(45) NOT NULL,modification TIMESTAMP NOT NULL);
INSERT INTO resource VALUES(1,'device','device','{accessibilityEnabled:false}',0,'resourceName','severity','type','03-09-2011');
DROP TABLE IF EXISTS resource_property;
CREATE TABLE resource_property ( id INTEGER PRIMARY KEY,resource_id INT NOT NULL,key VARCHAR(45) NOT NULL,value VARCHAR(500) NOT NULL);
DROP TABLE IF EXISTS resourcetype;
CREATE TABLE resourcetype ( id INTEGER PRIMARY KEY,name VARCHAR(45) NOT NULL,modification TIMESTAMP NOT NULL);
DROP TABLE IF EXISTS riskcommunication;
CREATE TABLE riskcommunication	 ( id INTEGER PRIMARY KEY,communication_sequence INT NOT NULL,risktreatment_id INT NOT NULL);
INSERT INTO riskcommunication VALUES(1,1,1);
DROP TABLE IF EXISTS risktreatment;
CREATE TABLE risktreatment ( id INTEGER PRIMARY KEY,textualdescription VARCHAR(1024) NOT NULL);
INSERT INTO risktreatment VALUES(1,'You are trying to disable accessibility, which is an important security mechanism for MUSES.
 This can cause the device having a lower level of security. MUSES will change this for you. In case you want to change it back, go to Settings > System (tab) > Accesibility > Services > MUSES and disable it');
DROP TABLE IF EXISTS role;
CREATE TABLE role ( id INTEGER PRIMARY KEY,description VARCHAR(1024) NOT NULL,modification TIMESTAMP NOT NULL);
DROP TABLE IF EXISTS sensor_configuration;
CREATE TABLE sensor_configuration	 ( id INTEGER PRIMARY KEY,sensor_type VARCHAR(45) NOT NULL,key VARCHAR(45) NOT NULL,value VARCHAR(45) NOT NULL);
INSERT INTO sensor_configuration VALUES(1,'CONTEXT_SENSOR_DEVICE_PROTECTION','trustedav','com.avast.android.mobilesecurity');
INSERT INTO sensor_configuration VALUES(2,'CONTEXT_SENSOR_DEVICE_PROTECTION','trustedav','Mobile Security & Antivirus');
INSERT INTO sensor_configuration VALUES(3,'CONTEXT_SENSOR_DEVICE_PROTECTION','trustedav','com.avira.android');
INSERT INTO sensor_configuration VALUES(4,'CONTEXT_SENSOR_DEVICE_PROTECTION','trustedav','com.symantec.mobilesecurity');
INSERT INTO sensor_configuration VALUES(5,'CONTEXT_SENSOR_DEVICE_PROTECTION','trustedav','com.cleanmaster.security');
INSERT INTO sensor_configuration VALUES(6,'CONTEXT_SENSOR_DEVICE_PROTECTION','enabled','true');
INSERT INTO sensor_configuration VALUES(7,'CONTEXT_SENSOR_LOCATION','mindistance','10');
INSERT INTO sensor_configuration VALUES(8,'CONTEXT_SENSOR_LOCATION','mindtime','400');
INSERT INTO sensor_configuration VALUES(9,'CONTEXT_SENSOR_LOCATION','radius','12');
INSERT INTO sensor_configuration VALUES(10,'CONTEXT_SENSOR_LOCATION','enabled','true');
INSERT INTO sensor_configuration VALUES(11,'CONTEXT_SENSOR_FILEOBSERVER','path','/SWE/');
INSERT INTO sensor_configuration VALUES(12,'CONTEXT_SENSOR_FILEOBSERVER','enabled','true');
INSERT INTO sensor_configuration VALUES(13,'CONTEXT_SENSOR_APP','enabled','true');
INSERT INTO sensor_configuration VALUES(14,'CONTEXT_SENSOR_CONNECTIVITY','enabled','true');
INSERT INTO sensor_configuration VALUES(15,'CONTEXT_SENSOR_INTERACTION','enabled','true');
INSERT INTO sensor_configuration VALUES(16,'CONTEXT_SENSOR_PACKAGE','enabled','true');
INSERT INTO sensor_configuration VALUES(17,'CONTEXT_SENSOR_SETTINGS','enabled','true');
INSERT INTO sensor_configuration VALUES(18,'CONTEXT_SENSOR_NOTIFICATION','enabled','true');
INSERT INTO sensor_configuration VALUES(19,'CONTEXT_SENSOR_LOCATION','zone','Office Valencia;1;500;-0.349593;39.467912');
INSERT INTO sensor_configuration VALUES(20,'CONTEXT_SENSOR_LOCATION','zone','Office Madrid;2;500;-3.682402;40.443132');
DROP TABLE IF EXISTS subject;
CREATE TABLE subject ( id INTEGER PRIMARY KEY,description VARCHAR(45) NOT NULL,role_id INT NOT NULL,modification TIMESTAMP NOT NULL);
DROP TABLE IF EXISTS user_credentials;
CREATE TABLE user_credentials	 ( id INTEGER PRIMARY KEY,device_id VARCHAR(45) NOT NULL,username VARCHAR(45) NOT NULL,password VARCHAR(45) NOT NULL);
INSERT INTO user_credentials VALUES(1,'359521065844450','y','y');
