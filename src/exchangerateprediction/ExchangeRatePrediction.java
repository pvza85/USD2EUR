/* Exchange Rate Prediction Using Machine Learning algorithms
 * authors: Payam Azad
 * Machine Learning course final Project - April 2013 
 * This program will use SVR for finding EU/USD exhange rate
 * I used Simple Genetic Algorithm for SVR parameter selection 
 */
package exchangerateprediction;

public class ExchangeRatePrediction 
{
    public static void main(String[] args) 
    {
        RatePredictor problem = null;
        if(args.length == 3)
            problem = new RatePredictor(args[0], args[1], args[2]);
        else  //TODO: ask file names from user
            problem = new RatePredictor("train.xls", "test.xls", "output");
        
        problem.prepare(0, -100, 100);  //number of previous days to get affected, scaling lower bound, upper bound
        problem.train();
        problem.predict(0); //number of days to predict (0 mean just predict today)
        
    }
}
