/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mepcotterell.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mepcotterell
 */
public class SimpleCache {
    
    private static final Logger log = Logger.getLogger(SimpleCache.class.getName());
    public static final int DEFAULT_TTL = 3600;
    
    private int ttl;
    private Map<String, SimpleCacheItem> map;
    
    public SimpleCache (int seconds) {
        this.ttl = seconds;
        this.map = new HashMap<String, SimpleCacheItem>();
    } // SimpleCache
    
    public SimpleCache () {
        this(SimpleCache.DEFAULT_TTL);
    } // SimpleCache
    
    public URL get (URL url) {
        
        // cleanup the cache
        this.cleanup();
        
        // get the key
        String key = url.toExternalForm();
        
        // check if the url is cached
        if (this.map.containsKey(key)) {
            log.info(String.format("Using cached version of %s (%s)", key, this.map.get(key).url.toExternalForm()));
            return this.map.get(key).url;
        } else {
            try {
                URL downloaded = download(url);
                SimpleCacheItem item = new SimpleCacheItem(downloaded);
                this.map.put(key, item);
                return downloaded;
            } catch (IOException ex) {
                log.warning(String.format("Unable to cache %s --> %s", key, ex));
                return url;
            } // try
        } // if
        
    } // get
    
    public void purge () {
        log.info("Cache has been purged");
        this.map = new HashMap<String, SimpleCacheItem>();
    } // purge
    
    private void cleanup () {
        for (String url : this.map.keySet()) {
            if (this.map.get(url).isExpired()) {
                log.info(String.format("Cache expired for %s", url));
                this.map.remove(url);
            } // if
        } // for
    } // cleanup
    
    private URL download (URL url) throws IOException {
        
        log.info(String.format("Preparing to download %s", url.toString()));
        
        // create a temp file
        File dir = new File(System.getProperty("java.io.tmpdir"));
        File temp = File.createTempFile("SimpleCache", null, dir);
        //temp.createNewFile();
        //temp.deleteOnExit();
        
        log.info(String.format("Setup to download %s to %s", url.toString(), temp.getPath()));
        
        // get the url inputstream
        InputStream input = url.openConnection().getInputStream();
        OutputStream output = new FileOutputStream(temp);
        
        byte[] buffer = new byte[4 * 1024];
        int bytes = 0;
        
        log.info(String.format("Downloading %s to %s", url.toString(), temp.getPath()));
        
        while ((bytes = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytes);
        } // while
        
        log.info(String.format("Successfully downloaded %s to %s", url.toString(), temp.getPath()));
        
        output.close();
        input.close();
        
        // return the new url
        return temp.toURI().toURL();
        
    } // download
    
    private class SimpleCacheItem {
        
        public URL url;
        public Date timestamp;
        
        public SimpleCacheItem (URL url) {
            this.url = url;
            this.timestamp = new Date();
        } // SimpleCacheItem
        
        public boolean isExpired() {
            Date d = new Date();
            return (d.getTime() > (this.timestamp.getTime() + ttl));
        } // isExpired
        
    } // SimpleCacheItem
    
} // SimpleCache 
