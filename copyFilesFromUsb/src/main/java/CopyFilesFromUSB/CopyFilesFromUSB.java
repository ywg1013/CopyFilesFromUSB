package CopyFilesFromUSB;

import org.apache.commons.io.FileUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Aingaran on 02-07-2017.
 * This is a class to copy files from removable drive to a local location.
 */
public class CopyFilesFromUSB {


    private final static String configfile = "cfg.ini";

    private final static String updatefile = "Nodehxy.bin";
    private static File uDisk = null;
    private int noOfDrivesAvailable;
    private List<File> oridisk = new ArrayList<File>();

    public static void main(String args[]) {
        CopyFilesFromUSB copyFilesFromUSB = new CopyFilesFromUSB();

        copyFilesFromUSB.setNoOfDrivesAvailable(File.listRoots().length);

        while (true) {
            if (copyFilesFromUSB.driveListChange(copyFilesFromUSB.getNoOfDrivesAvailable())) {

                File path = uDisk;
                boolean updateCfg = copyinifile(configfile, path.getAbsolutePath() + "cfg.ini");
                if (updateCfg) {
                    System.out.println("配置文件拷贝成功");
                } else {
                    System.out.println("配置文件拷贝失败");
                }
                boolean updateBin = copyfile(updatefile, path.getAbsolutePath());
                if (updateBin) {
                    System.out.println("升级文件拷贝成功");
                    System.out.println("-------------------------------------------------------------------------");
                } else {
                    System.out.println("升级文件拷贝失败");
                }


            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static boolean copyinifile(String src, String dest) {

        Path path = Paths.get(src);
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(path, Charset.defaultCharset());
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }

        Path dpath = Paths.get(dest);
        List<String> dlines = new ArrayList<>();
        try {
            dlines = Files.readAllLines(dpath, Charset.defaultCharset());
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }

        if (dlines.size() == 0) {
            return false;
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(dest);
        } catch (IOException e) {
            return false;
        }
        BufferedWriter bufferedWriter = new BufferedWriter(writer);
        try {
            bufferedWriter.write(dlines.get(0));
            System.out.println("******************  write id = " + Integer.toHexString(Integer.valueOf(dlines.get(0))) + "  ******************");
        } catch (IOException e) {
            return false;
        }
        for (int i = 1; i < lines.size(); i++) {
            try {
                bufferedWriter.newLine();
                bufferedWriter.write(lines.get(i));
            } catch (IOException e) {

                return false;
            }

        }

        try {
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            return false;
        }
        return true;

    }

    private static boolean copyfile(String src, String dest) {
        try {
            FileUtils.copyFileToDirectory(new File(updatefile), new File(dest),false);
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }

        return true;
    }

    private int getNoOfDrivesAvailable() {
        return this.noOfDrivesAvailable;
    }

    private void setNoOfDrivesAvailable(int noOfDrivesAvailable) {
        this.noOfDrivesAvailable = noOfDrivesAvailable;
        oridisk.addAll(List.of(File.listRoots()));
    }

    private boolean driveListChange(int noOfDrivesAvailable) {
        if (File.listRoots().length != noOfDrivesAvailable) {
            if (File.listRoots().length > noOfDrivesAvailable) {
                this.noOfDrivesAvailable = File.listRoots().length;
                List<File> newdisk = new ArrayList<>(Arrays.asList(File.listRoots()));
                newdisk.removeAll(oridisk);
                if (newdisk.size() > 0) {
                    uDisk = newdisk.get(0);
                    System.out.println("Add Driver : " + uDisk);
                }
                oridisk = newdisk;
                return true;
            } else {
                List<File> newdisk = new ArrayList<>(Arrays.asList(File.listRoots()));
                oridisk.removeAll(newdisk);
                if (oridisk.size() > 0) {
                    System.out.println("Remove Driver : " + oridisk.get(0));
                }
                oridisk = newdisk;
                this.noOfDrivesAvailable = File.listRoots().length;
                return false;
            }
        }
        return false;
    }


    private void listFilesAndFilesSubDirectories(String directoryName, ArrayList<String> files) {
        File directory = new File(directoryName);
        //get all the files from a directory
        File[] fileList = directory.listFiles();
        for (File file : fileList) {
            if (file.isFile()) {
                files.add(file.getAbsolutePath());
            } else if (file.isDirectory()) {
                listFilesAndFilesSubDirectories(file.getAbsolutePath(), files);
            }
        }
    }
}
