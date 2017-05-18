package main;

import core.job.DefaultJob;
import core.modules.Module;
import core.parameters.ParameterSet;
import core.parameters.parametertypes.StringParameter;
import gui.jobinfo.JobInfo;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tctupangiu on 17/05/2017.
 */
public class TestJobInfo {

    @Test
    public void testJobInfo() {
        String filename = "C:\\Users\\tctupangiu\\Desktop\\test\\LR-SSI-74_FB_Y_Z_2__A330CeoRR__PyW--SR--MS_medium_TF_tse.html";

        assert getFileType(filename) == 3;
    }

    private int getFileType(String filename) {

        Pattern batchPattern = Pattern.compile("o[0-9]+$");
        Pattern codePattern = Pattern.compile("py$");
        Pattern htmlPattern = Pattern.compile("html$");

        if (batchPattern.matcher(filename).find()) {
            //is the batch file
            return 1;
        }
        else if (codePattern.matcher(filename).find()) {
            return 2;
        }
        else if (htmlPattern.matcher(filename).find()) {
            return 3;
        }

        return 0;
    }
}