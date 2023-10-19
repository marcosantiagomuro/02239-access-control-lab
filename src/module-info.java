/**
 * 
 */
/**
 * 
 */

module client_server {
	exports server to java.rmi;
    exports remoteInterface to java.rmi;
    exports server.enums to java.rmi;

    requires java.rmi;
	requires java.desktop;
    requires lombok;
    requires org.apache.commons.lang3;
}
