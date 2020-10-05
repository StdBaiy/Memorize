package stdbay.memorize.util;

import android.util.Log;

import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DeleteUtil {

    /** 删除文件，可以是文件或文件夹
     * @param media 要删除的文件夹或文件名
     */
    public static void delete(LocalMedia media) {
        List<String>paths=new ArrayList<>();
        if(media.getCutPath()!=null)
            paths.add(media.getCutPath());

        for(String delFile:paths) {
            File file = new File(delFile);
            if (file.exists()){
                if (file.isFile()) {
                    deleteSingleFile(delFile);
                } else {
                    deleteDirectory(delFile);
                }
            }
        }
    }

    /** 删除单个文件
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    private static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.e("--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
//                //Toast.makeText(MyApplication.getContext(), "删除单个文件" + filePath$Name + "失败！", //Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            //Toast.makeText(MyApplication.getContext(), "删除单个文件失败：" + filePath$Name + "不存在！", //Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /** 删除目录及目录下的文件
     * @param filePath 要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    private static boolean deleteDirectory(String filePath) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            //Toast.makeText(MyApplication.getContext(), "删除目录失败：" + filePath + "不存在！", //Toast.LENGTH_SHORT).show();
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(file
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            //Toast.makeText(MyApplication.getContext(), "删除目录失败！", //Toast.LENGTH_SHORT).show();
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            Log.e("--Method--", "Copy_Delete.deleteDirectory: 删除目录" + filePath + "成功！");
            return true;
        } else {
            //Toast.makeText(MyApplication.getContext(), "删除目录：" + filePath + "失败！", //Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}

