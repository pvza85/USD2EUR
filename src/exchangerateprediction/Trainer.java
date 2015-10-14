/*
 */
package exchangerateprediction;

import java.io.IOException;

public class Trainer 
{
    byte[] parameters;
    String trainFile;
    public Trainer(String fileName)
    {
        trainFile = fileName;
    }
    public double[] run(byte[] parameters) throws IOException
    {
        String[] argv = extractParameters(parameters);
        svm_train t = new svm_train();
        System.out.print(toString(argv));
	return t.run(argv);
    }
    public String[] extractParameters(byte[] parameters)
    { 
        String type, kernal, degree, gamma;
        
        if(parameters[0] == 0)
            type = "3";
        else
            type = "4";
        
        int t = 0;
        for(int i = 1; i <= 2; i++)
            t += parameters[i] * Math.pow(2, 2-i);
        kernal = String.valueOf(t);
        
        t = 0;
        for(int i = 3; i <= 6; i++)
            t += parameters[i] * Math.pow(2, 6-i);
        t -= 4;
        degree = String.valueOf(Math.pow(2, t));
        
        t = 0;
        for(int i = 7; i <= 10; i++)
            t += parameters[i] * Math.pow(2, 10-i);
        t -= 12;
        gamma = String.valueOf(Math.pow(2, t));
        //TODO: add more parameters
        String[] result = {"-s", type, "-t", kernal, "-c", degree, "-g", gamma, "-h", "0", "-v", "3", "-q", trainFile};
        return result;
    }
    public String toString(String[] parameters)
    {
        String res = "\n@";
        if("3".equals(parameters[1]))
            res += "epsilon-SVR ";
        else
            res += "nu-SVR ";
        
        res += "with ";
        switch (parameters[3]) 
        {
            case "0":
                res += "linear ";
                break;
            case "1":
                res += "polynomial ";
                break;
            case "2":
                res += "RBF ";
                break;
            case "3":
                res += "sigmoid ";
                break;
        }
        
        res += "kernal.";
        
        res += " C = ";
        res += parameters[5];
        
        res += " gamma = ";
        res += parameters[7];
        res += "\n";
        
        return res;
    }
}
