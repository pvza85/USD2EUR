package exchangerateprediction;

import java.io.IOException;

public class GATrainer 
{
    //TODO: add EDA learner, add hill climbing,...
    String trainFile;
    public GATrainer(String train)
    {
        trainFile = train;
    }
    public void run() throws IOException
    {
        GeneticAlgorithm ga = new GeneticAlgorithm(11, 10);  //chromosome lengh = 11  population size = 10
        ga.trainFile = trainFile;        
        byte[] bestAnswer = ga.run(50);  //50 generations
        ga.printResults();
        
        //last and best run
        Trainer trainer = new Trainer(trainFile);
        double[] result = trainer.run(bestAnswer);
        String[] parameters = trainer.extractParameters(bestAnswer);
        System.out.print("The best training done by: " + result[0] + " MSE and " + result[1] + " SCC in cross validations with parameters: \n");
        System.out.print(trainer.toString(parameters));
        
    }
    
    
}
