/*
 * Copyright (c) 2005-2006, John Mettraux, OpenWFE.org
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 * . Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.  
 * 
 * . Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * . Neither the name of the "OpenWFE" nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without
 *   specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * $Id: Wget.java 3077 2006-08-30 06:01:05Z jmettraux $
 */

//
// Wget.java
//
// john.mettraux@openwfe.org
//
// generated with 
// jtmpl 1.1.01 2004/05/19 (john.mettraux@openwfe.org)
//

package util;

import java.net.URL;
import java.net.URLConnection;


/**
 * A java implementation of 'wget'.
 *
 * <p><font size=2>CVS Info :
 * <br>$Author: jmettraux $
 * <br>$Id: Wget.java 3077 2006-08-30 06:01:05Z jmettraux $ </font>
 *
 * @author john.mettraux@openwfe.org
 */
public class Wget
{

    /*
    private final static org.apache.log4j.Logger log = org.apache.log4j.Logger
        .getLogger(Wget.class.getName());
    */

    //
    // CONSTANTS & co

    /**
     * How many bytes should get read at once ?
     */
    public final static int READ_BUFFER_SIZE = 2048;

    public final static String BANNER 
        = "OpenWFE Wget 0.0.5 - simple java wget";

    /**
     * After 7 unsuccessful reads on the wire, Wget will consider the download
     * as done.
     */
    public final static int MAX_RETRIES = 42;

    //
    // FIELDS

    //
    // CONSTRUCTORS

    //
    // METHODS

    //
    // STATIC METHODS

    /**
     * Same as wget(d, u), but verbosity is off.
     *
     * @return true if the downloading actually happened
     */
    public static boolean wget 
        (final String downloadDir, final String sUrl)
    throws Exception
    {
        return wget(downloadDir, sUrl, false, false);
    }

    /**
     * This method is public so that applications may use it (as a library).
     *
     * @return true if the downloading actually happened
     */
    public static boolean wget 
        (String downloadDir, 
         final String sUrl, 
         final boolean head,
         final boolean verbose)
    throws 
        Exception
    {
        if ( ! downloadDir.endsWith(java.io.File.separator))
            downloadDir += java.io.File.separator;

        int i = sUrl.lastIndexOf("/");
        String fileName = sUrl.substring(i+1);
        fileName = downloadDir + fileName;

        final java.io.File targetFile = new java.io.File(fileName);

        final URL url = new URL(sUrl);

        final URLConnection con = url.openConnection();

        final long remoteLastModified = con.getLastModified();

        if (head && targetFile.exists() && verbose)
        {
            System.out.println("..wget() local   "+targetFile.lastModified());
            System.out.println("..wget() remote  "+remoteLastModified);
        }

        if (head &&
            targetFile.exists() &&
            remoteLastModified != 0 &&
            targetFile.lastModified() >= remoteLastModified)
        {
           if (verbose)
           {
               System.out.println
                   ("..wget() local file fresher than web version. "+
                    "Not downloading.");
           }

           return false;
        }

        if (verbose)
            System.out.println("..wget() will save to "+fileName);

        int contentLength = con.getContentLength();

        if (verbose)
            System.out.println("..wget() contentLength is "+contentLength);

        if (contentLength < 0) contentLength = Integer.MAX_VALUE;

        final java.io.InputStream is = url.openStream();

        final java.io.FileOutputStream fos = 
            new java.io.FileOutputStream(fileName);

        byte[] buffer = new byte[READ_BUFFER_SIZE];

        int totalRead = 0;
        int retries = 0;

        while (true)
        {
            int read = is.read(buffer);

            totalRead += read;

            if (verbose)
            {
                System.out.println
                    ("..wget() read "+read+
                     " bytes   ("+totalRead+"/"+contentLength+") r"+retries);
            }

            if (read > 0)
            {
                fos.write(buffer, 0, read);
                fos.flush();

                retries = 0;
            }

            if (totalRead >= contentLength) break;

            if (read < READ_BUFFER_SIZE)
            {
                if (retries >= MAX_RETRIES)
                {
                    if (verbose)
                        System.out.println("..wget() giving up.");

                    break;
                }

                if (read < 1)
                {
                    //Thread.sleep(14);
                    Thread.yield();
                    retries++;
                }
            }
        }

        //fos.flush();
        fos.close();
        is.close();

        return true;
    }

    private static void mkdir (final String dir, final boolean verbose)
        throws Exception
    {
        final java.io.File f = new java.io.File(dir);

        if (f.exists() && ( ! f.isDirectory()))
        {
            throw new IllegalArgumentException
                ("dir '"+dir+"' already exists and it's not a directory.");
        }

        if (f.exists())
        {
            if (verbose)
                System.out.println("..wget() dir '"+dir+"' already present");

            return;
        }

        f.mkdirs();

        if (verbose)
            System.out.println("..wget() made dir '"+dir+"'");
    }

    /**
     * Reads the file behind the URL, which should be an enumeration
     * of files to further download..
     * <br>
     * example :<br>
     * <pre>
     * #
     * # download list
     *
     * . http://remote.host.tld/images/icon1.png
     * . http://remote.host.tld/content/text2.xml
     * mkdir html
     * mkdir icons
     * html http://other.remote.host.tld/index.htm
     * server http://host1/
     * . index1.htm
     * icons/ icon1.png
     * server http://host2/
     * . index2.htm
     * </pre>
     * <br>
     * 'mkdir' will create a local directory<br>
     * 'server' allows for lighter lists
     */
    public static void downloadList 
        (String downloadListUrl,
         final boolean head,
         final boolean verbose)
    throws 
        Exception
    {
        if (downloadListUrl.indexOf("://") < 0)
            downloadListUrl = "file:" + downloadListUrl;

        final URL dUrl = new URL(downloadListUrl);

        final URLConnection con = dUrl.openConnection();

        final java.io.BufferedReader br = new java.io.BufferedReader
            (new java.io.InputStreamReader(con.getInputStream()));

        String server = null;

        while (true)
        {
            String line = br.readLine();

            if (line == null) break;

            line = line.trim();

            if (line.length() < 1 || line.startsWith("#")) continue;

            if (line.startsWith("mkdir "))
            {
                mkdir(line.substring(6).trim(), verbose);
                continue;
            }
            if (line.startsWith("server"))
            {
                if (line.trim().length() == 6)
                    server = null;
                else
                    server = line.substring(7).trim();

                if (server != null && ( ! server.endsWith("/")))
                    server += "/";

                continue;
            }

            final int i = line.indexOf(" ");

            String downloadDir = ".";
            String urlToDownload = line;

            if (i > -1)
            {
                downloadDir = line.substring(0, i).trim();
                urlToDownload = line.substring(i+1).trim();
            }

            if (server != null && urlToDownload.indexOf("://") < 1)
            {
                urlToDownload = server + urlToDownload;
            }

            if (verbose)
            {
                //System.out.println
                //    ("..wget() >"+line+"<");
                System.out.println
                    ("..wget() todir >"+downloadDir+
                     "<  fromURL >"+urlToDownload+"<");
            }

            final boolean b = wget(downloadDir, urlToDownload, head, verbose);

            if (b)
                System.out.println("x "+urlToDownload);
            else
                System.out.println(". "+urlToDownload);
        }
    }

    private static void printUsage ()
    {
        final String cmd = "java "+Wget.class.getName();

        System.out.println();
        System.out.println(BANNER);
        System.out.println();
        System.out.println("USAGE :");
        System.out.println();
        System.out.print  (cmd);
        System.out.println(" [-d {downloadDir}] [-v] [-H] {URL}*");
        System.out.println(" [-v] [-H] -l {URL of a download list}");
        System.out.println();
        System.out.println("Wget is a java 'wget', with not much features.");
        System.out.println();
        System.out.println("  -v : verbose");
        System.out.println("  -h : prints this usage and exits");
        System.out.println("  -H : HEAD, will not download if local resource fresher than web resource");
        System.out.println("  -l : the files and their target dir are enumerated in a list");
        System.out.println("       (behind a URL or within a local file)");
        System.out.println();

        System.exit(-1);
    }

    public static void main (final String[] args)
        throws Exception
    {
        if (args.length < 1) printUsage();

        boolean head = false;
        boolean verbose = false;

        int index = 0;

        String downloadDir = ".";
        String downloadList = null;

        while (index < args.length && args[index].startsWith("-"))
        {
            if (args[index].equals("-d"))
            {
                if (args.length - index < 2) printUsage();

                downloadDir = args[index+1];
                index += 2;

            }
            else if (args[index].equals("-v"))
            {
                verbose = true;
                index++;

                //System.out.println("...verbose");
            }
            else if (args[index].equals("-H"))
            {
                head = true;
                index++;
            }
            else if (args[index].equals("-h"))
            {
                printUsage();
            }
            else if (args[index].equals("-l"))
            {
                downloadList = args[index+1];
                index += 2;
            }
        }

        if (downloadList != null)
        {
            System.out.println("...downloadList is at "+downloadList);
            downloadList(downloadList, head, verbose);
            System.exit(0);
        }

        //
        // do download

        for (int i=index; i<args.length; i++)
        {
            //System.out.println("args["+i+"] is >"+args[i]+"<");
            
            wget(downloadDir, args[i], head, verbose);
            System.out.println("...got "+args[i]);
        }
    }

}
