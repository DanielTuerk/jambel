package com.jambit.jambel.config;

import java.io.IOException;
import java.nio.file.*;

/**
 * @author Daniel Tuerk (daniel.tuerk@jambit.com)
 */
public class  ConfigPathWatchService extends Thread {

    public void todo(){
        //TODO
                                       String configFilePath="";
        Path path = Paths.get(configFilePath);
        WatchService watchService = null;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            WatchKey watchKey = path.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_DELETE,StandardWatchEventKinds.ENTRY_MODIFY);
            try {
                watchService.take();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                //TODO
                return;
            }

            for (WatchEvent<?> event: watchKey.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // This key is registered only
                // for ENTRY_CREATE events,
                // but an OVERFLOW event can
                // occur regardless if events
                // are lost or discarded.
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                // The filename is the
                // context of the event.
                WatchEvent<Path> ev = (WatchEvent<Path>)event;
                Path filename = ev.context();

                // Verify that the new
                //  file is a text file.
                try {
                    // Resolve the filename against the directory.
                    // If the filename is "test" and the directory is "foo",
                    // the resolved name is "test/foo".
                    Path child = path.resolve(filename);
                    if (!Files.probeContentType(child).equals("text/plain")) {
                        System.err.format("New file '%s'" +
                                " is not a plain text file.%n", filename);
                        continue;
                    }
                } catch (IOException x) {
                    System.err.println(x);
                    continue;
                }

                // Email the file to the
                //  specified email alias.
                System.out.format("Emailing file %s%n", filename);
                //Details left to reader....



            }

            // Reset the key -- this step is critical if you want to
            // receive further watch events.  If the key is no longer valid,
            // the directory is inaccessible so exit the loop.
            boolean valid = watchKey.reset();
            if (!valid) {
                throw new RuntimeException("watch key no longer valid");
            }




        } catch (IOException e) {
            throw new RuntimeException("can't register watch service for the config path: "+configFilePath, e);
        }
    }
}
