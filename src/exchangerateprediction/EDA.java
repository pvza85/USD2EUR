/* Author: Payam Azad    May 2013
 *This is an implementation of simplest Estimation of Distribution Algorithms (EDA)
 */

package exchangerateprediction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Formatter;
import java.util.Random;

public class EDA 
{
    int populationSize, maxGeneration;
    int chromosomeLength;
    String fileName;
    int[] checkedPoints;
    double[] checkedFitness;
    int checkedNumber;
    
    byte[][] population;
    double[] fitness;
    double[] probabilities;
    
    public EDA(int chromosome, int population)
    {
        populationSize = population;
        chromosomeLength = chromosome;
        chromosomeLength = 11;      //just now that we know we have 11 parameters to change
    }
    
    int dynamicCounter;
    //evaluate fitness of each population member,, this function have to be overwritten
    private double evaluate(byte[] solution) throws IOException
    {
        int t = arrtoi(solution);
        for(int i = 0; i < checkedNumber; i++)
            if(t == checkedPoints[i])
            {
                System.out.println("$$$ " + dynamicCounter++);
                return checkedFitness[i];
            }
        Trainer trainer = new Trainer(fileName);
        Random rand = new Random(System.currentTimeMillis());
        double res = trainer.run(solution)[0];
        //TODO: add SCC affect also, find classification result
        checkedPoints[checkedNumber++] = arrtoi(solution);
        checkedFitness[checkedNumber] = res;
        
        return res;
    }
    
    int arrtoi(byte[] input)
    {
        int res = 0;
        
        for(int i = input.length-1, j = 0; i >= 0; i--, j++)
            res += Math.pow(j, input[i]);
        
        return res;
    }
    
    public byte[] run(int generation) throws IOException
    {
        maxGeneration = generation;
        initialize();
        
         for(int i = 0; i < maxGeneration; i++)
        {
            System.out.println("\n#GENERATION: " + (i+1));
            findProbabilities();
            createNewGeneration();
            evaluateAll();
            sort();
            //log();
            if(stoppingCriteria())
                break;
        }
        
        return population[0];
    }
    
    private void initialize() throws IOException
    { 
        population = new byte[populationSize][chromosomeLength];
        fitness = new double[populationSize];
        
        checkedPoints = new int[maxGeneration * populationSize];
        checkedFitness = new double[maxGeneration * populationSize];
        checkedNumber = 0;
        
        
        System.out.println("\n#GENERATYION:" + 0);
        //initilize first population and sort them and select 
        Random rand = new Random(System.currentTimeMillis());
        
         for(int j = 0; j < chromosomeLength; j++)
                population[0][j] = 0;  
         System.out.println("#population:" + 0);
         fitness[0] = evaluate(population[0]);
         
         for(int j = 0; j < chromosomeLength; j++)
                population[1][j] = 1;   
         System.out.println("\n#population:" + 1);
         fitness[1] = evaluate(population[1]);
         
        //create random solutions
        for(int i = 2; i < populationSize; i++)  //*** enhance it by checking repeated solutions
        {
            System.out.println("\n#population:" + i);
            for(int j = 0; j < chromosomeLength; j++)
                population[i][j] = (byte)rand.nextInt(2);  //*** make it faster by random creation methods
            
            //finding fitness of each random solution
            fitness[i] = evaluate(population[i]);
        }
        
        sort();            //sort popultation members for easier selection and elitism
    } 
    private void evaluateAll() throws IOException
    {
        for(int i = 0; i < populationSize; i++) 
        {
            System.out.println("\n#population: " + i);
            fitness[i] = evaluate(population[i]);
        }
    }
    private void sort() throws IOException
    {
        double[] tempFitness = new double[populationSize];
        System.arraycopy( fitness, 0, tempFitness, 0, populationSize );
        byte[][] tempPopulation = new byte[populationSize][chromosomeLength];
        //System.arraycopy( population, 0, tempPopulation, 0, population.length );
        
        //Arrays.sort(tempFitness);
        
        //place population in order in tempPopulation
        /*for(int i = populationSize-1;  i >= 0; i--)
            for(int j = 0; j < populationSize; j++)
                if(tempFitness[i] == fitness[j])
                {
                    fitness[j] = -1;
                    System.arraycopy(population[j], 0, tempPopulation[populationSize - i - 1], 0, chromosomeLength);
                    break;
                }*/
        
        for(int i = 0; i < populationSize; i++)
            for(int j = 0; j < populationSize-1; j++)
            {
                if(fitness[j] >  fitness[j+1])
                {
                    double t = fitness[j+1];
                    fitness[j+1] = fitness[j];
                    fitness[j] = t;
                    
                    byte[] T = new byte[chromosomeLength];
                    System.arraycopy(population[j+1], 0, T, 0, chromosomeLength);
                    System.arraycopy(population[j], 0, population[j+1], 0, chromosomeLength);
                    System.arraycopy(T, 0, population[j], 0, chromosomeLength);
                }
            }
        
        //population = tempPopulation;
        //evaluateAll();
        
        try (Formatter log = new Formatter(new FileWriter("generation_bests", true))) 
        {
            log.format("%f ", fitness[0]);
        }
        
    }
    private boolean stoppingCriteria()
    {
        return false;
    }
    private void findProbabilities()
    {
        probabilities = new double[chromosomeLength];
        
        for(int i = 0; i < chromosomeLength; i++)
        {
            int count = 0;
            for(int j = 0; j < populationSize / 2; j++)
                count += population[j][i];
            probabilities[i] =(double) count / populationSize * 2;
        }
        
    }
    private void createNewGeneration()
    {
        Random rand = new Random(System.currentTimeMillis());
        
        //from 1 because I want to preserve previous generations best memeber
        for(int i = 1; i < populationSize; i++)
            for(int j = 0; j < chromosomeLength; j++)
            {
                double r = rand.nextDouble();
                if(r < probabilities[j])
                    population[i][j] = 1;
                else
                    population[i][j] = 0;
            }
    }
    
    public String printResults()
    {
        String res = "Best Result with fitness of " + fitness[0] + "\n";
        
        return res;
    }
    
}
