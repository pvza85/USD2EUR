/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exchangerateprediction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Arrays;
import java.util.Formatter;

//***this package is based on byte array, I may be able to write a bitwise version
public class GeneticAlgorithm 
{
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
        Trainer trainer = new Trainer(trainFile);
        double res = trainer.run(solution)[0];
        //TODO: add SCC affect also, find classification result
        checkedPoints[checkedNumber++] = arrtoi(solution);
        checkedFitness[checkedNumber] = res;
        
        return res;
    }
   
    
    
    
    
    
    //--------------------variables------------------------------------
    private int populationSize;   //size of choromosome numbers
    private int chromosomeLength;   //size of each chormosome
    private double mutuationProbability;
    private double crossOverProbability;
    private int eliteNumber;     //number of elite answers that have to remain
    private int maxGeneration;
    
    //int mode;   //operate in byte mode or bit mode
    
    //Different methods of Selection, Cross Over and Mutation
    //***use enum later
    private int selectionMethod;
    private int crossOverMethod;
    private int mutuationMethod;
    
    //population 
    byte[][] population;
    //int[] bitPopulation;
    double[] fitness;
    byte[][] elites;
    int[] selectedParents;
    int[] checkedPoints;
    double[] checkedFitness;
    int checkedNumber;
    
    //process time calculation variables
    long runTime;
    long initializationTime;
    long selectionTime;
    long crossOverTime;
    long mutuationTime;
    long elitismTime;
    long evaluateTime;
    long sortTime;
    long otherTime;
    //---------------------------end of variable-------------------------
    
    public String trainFile;
    
    //-------------------------constructors------------------------------
    public GeneticAlgorithm(int chromosomeLength)
    {
        this.chromosomeLength = chromosomeLength;
        
        //default values
        this.populationSize = 10;        //***defualt population size I put 20, may change later
        this.mutuationProbability = (double)1/chromosomeLength;  //L*Pm = 1: expecting one mutuation in each chromosome
        this.selectionMethod = 1;       //simplest selection method, choosing best parents
        this.crossOverMethod = 1;       //simplest cross over method, one point cross over from half
        this.crossOverProbability = 0.8;  //cross over happens in any case (all offspring created sexually)
        this.eliteNumber = 1;           //default number of elite members that have to remain for next generation
        this.maxGeneration = 50;      //***default maximum number of generations, may change later
        
        //create population
        population = new byte[populationSize][chromosomeLength];
        fitness = new double[populationSize];
        elites = new byte[eliteNumber][chromosomeLength];
    }
    public GeneticAlgorithm(int chromosomeSize, int populationSize)
    {
        this(chromosomeSize);
        this.populationSize = populationSize;
        
        //create population
        population = new byte[populationSize][chromosomeLength];
        fitness = new double[populationSize];
    }
    public GeneticAlgorithm(int chromosomeSize, int populationSize, double mutuationProbability)
    {
        this(chromosomeSize, populationSize);
        this.mutuationProbability = mutuationProbability;
        
        //create population
        population = new byte[populationSize][chromosomeLength];
        fitness = new double[populationSize];
        elites = new byte[eliteNumber][chromosomeLength];
    }
    public GeneticAlgorithm(int chromosomeSize, int populationSize, double mutuationProbability, int selectionMethod, int crossOverMethod)
    {
        this(chromosomeSize, populationSize, mutuationProbability);
        this.selectionMethod = selectionMethod;
        this.crossOverMethod = crossOverMethod;
        
        //create population
        population = new byte[populationSize][chromosomeLength];
        fitness = new double[populationSize];
        elites = new byte[eliteNumber][chromosomeLength];
    }
    public GeneticAlgorithm(int chromosomeSize, int populationSize, double mutuationProbability, int selectionMethod, int crossOverMethod, double crossOverProbability)
    {
        this(chromosomeSize, populationSize, mutuationProbability, selectionMethod, crossOverMethod);
        this.crossOverProbability = crossOverProbability;
        
        //create population
        population = new byte[populationSize][chromosomeLength];
        fitness = new double[populationSize];
        elites = new byte[eliteNumber][chromosomeLength];
    }
    public GeneticAlgorithm(int chromosomeSize, int populationSize, double mutuationProbability, int selectionMethod, int crossOverMethod, double crossOverProbability, int eliteNumber)
    {
        this(chromosomeSize, populationSize, mutuationProbability, selectionMethod, crossOverMethod, crossOverProbability);
        this.eliteNumber = eliteNumber;
        
        //create population
        population = new byte[populationSize][chromosomeLength];
        fitness = new double[populationSize];
        elites = new byte[eliteNumber][chromosomeLength];
    }
    
    //---------------------public functions--------------------------------
    //main function to run genetic algorithm for default numbers of iteration
    public byte[] run() throws IOException
    {
        //***max Generation is 1000 for default
        return run(maxGeneration);
    }
    public byte[] run(int maxGeneration) throws IOException
    {
        //main function to run genetic algorithm
        long startTime, t; //for process time calculation
        
        
        startTime = System.currentTimeMillis();
        
        t = System.currentTimeMillis();
        initialize();   //initialize first gereneration, sort and select elites
        initializationTime = System.currentTimeMillis() - t;
        
        for(int i = 0; i < maxGeneration; i++)
        {
            System.out.println("\n#GENERATION: " + (i+1));
            t = System.currentTimeMillis();
            selection();
            selectionTime += System.currentTimeMillis() - t;
            
            t = System.currentTimeMillis();
            crossOver();
            crossOverTime += System.currentTimeMillis() - t;
            
            t = System.currentTimeMillis();
            mutuation();
            mutuationTime += System.currentTimeMillis() - t;
            
            t = System.currentTimeMillis();
            elitism();
            elitismTime += System.currentTimeMillis() - t;
            
            if(stoppingCriteria())
                break;
        }
        
        runTime = System.currentTimeMillis() - startTime;
        
        return population[0];  //return the best of population, that is after sorting the first one in population
    }
    public String printResults()
    {
        elitismTime -= (evaluateTime + sortTime);
        
        String res = "Best Result with fitness of " + fitness[1] + "\n created in: "
        + maxGeneration + " Generations and "
        + (double)runTime/1000 + " seconds, \n In details: \n"
        + "   Initialization: " + (double)initializationTime/1000 + " seconds (" + (100 * initializationTime / runTime) + "%) \n"
        + "   Selection: " + (double)selectionTime/1000 + " seconds (" + (100 * selectionTime / runTime) + "%) \n"
        + "   Cross Over: " + (double)crossOverTime/1000 + " seconds (" + (100 * crossOverTime / runTime) + "%) \n"
        + "   Mutuation: " + (double)mutuationTime/1000 + " seconds (" + (100 * mutuationTime / runTime) + "%) \n"
        + "   Evaluation: " + (double)evaluateTime/1000 + " seconds (" + (100 * evaluateTime / runTime) + "%) \n"
        + "   Sort: " + (double)sortTime/1000 + " seconds (" + (100 * sortTime / runTime) + "%) \n"
        + "   Elitism: " + (double)elitismTime/1000 + " seconds (" + (100 * elitismTime / runTime) + "%) \n";

        return res;
    }
    
    //-------------------------Initilization-------------------------
    private void initialize() throws IOException
    { 
        checkedPoints = new int[maxGeneration * populationSize];
        checkedFitness = new double[maxGeneration * populationSize];
        checkedNumber = 0;
        
        
        System.out.println("\n#GENERATYION:" + 0);
        //initilize first population and sort them and select 
        Random rand = new Random(System.currentTimeMillis());  //*** enhance it by faster better random creation methods 
        
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
        selectElites();    //select the elite members and keep them for next generation
    }
    
    //-----------------------Selections------------------------------
    private void selection()
    {
        //selection function that call different selection methods
        switch(selectionMethod)
        {
            case 0:
                selectBest();
                break;
            case 1:
                //***** tournament size 
                tournamentSelection(2);
                break;
            default:
                tournamentSelection(2);
                break;
        }
    }
    private void selectBest()
    {
        //simplest, naive selection method, it select the best parents
        selectedParents = new int[populationSize];
        //*** check for odd population
        for(int i = 0, j = populationSize/2-1; i < populationSize/2; i++, j--)
        {
            selectedParents[i*2] = i;
            selectedParents[i*2+1] = j;
        }
    } 
    private void tournamentSelection(int tournamentSize)
    {
        selectedParents = new int[populationSize];

        Random rand = new Random(System.currentTimeMillis());
        
        for(int i = 0; i < populationSize; i++)
        {
            int best = rand.nextInt(populationSize);
            for(int j = 1; j < tournamentSize; j++)
            {
                int t = rand.nextInt(populationSize);
                if(fitness[best] > fitness[t])
                    best = t;
            }
            selectedParents[i] = best;
        }
        
    }
    
    //------------------Cross Over----------------------------
    private void crossOver()
    {
        switch(crossOverMethod)
        {
            case 0:
                simpleOnePointCrossOver();
                break;
            case 1:
                onePointCrossOver();
                break;
            case 2:
                twoPointCrossOver();
                break;
            default:
                twoPointCrossOver();
                break;              
        }
    }
    //simplest cross over possible, one point from middle of each answer
    private void simpleOnePointCrossOver()
    {
        byte[][] offSprings = new byte[populationSize][chromosomeLength];
        //****check of odd chromosomeLength
        for(int i = 0; i < populationSize; i = i+2)
        {
            int j;
            for(j = 0; j < chromosomeLength/2; j++)
            {
                offSprings[i][j] = population[selectedParents[i]][j];
                offSprings[i+1][j] = population[selectedParents[i+1]][j];
            }
            for(;j < chromosomeLength; j++)
            {
                offSprings[i][j] = population[selectedParents[i+1]][j];
                offSprings[i+1][j] = population[selectedParents[i]][j];
            }
        }
    }  
    //one point cross over, find cross over point by random
    private void onePointCrossOver()
    {
        byte[][] offSprings = new byte[populationSize][chromosomeLength];
        //****check of odd chromosomeLength
        for(int i = 0; i < populationSize; i = i+2)
        {
            int j;
            Random rand = new Random(System.currentTimeMillis());
            int x = rand.nextInt(chromosomeLength);
            
            for(j = 0; j < x; j++)
            {
                offSprings[i][j] = population[selectedParents[i]][j];
                offSprings[i+1][j] = population[selectedParents[i+1]][j];
            }
            for(;j < chromosomeLength; j++)
            {
                offSprings[i][j] = population[selectedParents[i+1]][j];
                offSprings[i+1][j] = population[selectedParents[i]][j];
            }
        }
    }  
    private void twoPointCrossOver()
    {
        byte[][] offSprings = new byte[populationSize][chromosomeLength];
        Random rand = new Random(System.currentTimeMillis());
        
        
        
        for(int i = 0; i < populationSize; i = i+2)
        {
            int j;
            
            if(rand.nextDouble() > crossOverProbability)
            {
                for(j = 0; j < chromosomeLength; j++)
                {
                    offSprings[i][j] = population[selectedParents[i]][j];
                    offSprings[i+1][j] = population[selectedParents[i+1]][j];
                }
                continue;
            }
            
            int x = rand.nextInt(chromosomeLength);
            int y = rand.nextInt(chromosomeLength);
            
            //swap x and y
            if(x > y)
            {
                int t = x;
                x = y;
                y = t;
            }
            
            for(j = 0; j < x; j++)
            {
                offSprings[i][j] = population[selectedParents[i]][j];
                offSprings[i+1][j] = population[selectedParents[i+1]][j];
            }
            for(;j < y; j++)
            {
                offSprings[i][j] = population[selectedParents[i+1]][j];
                offSprings[i+1][j] = population[selectedParents[i]][j];
            }
            for(;j < chromosomeLength; j++)
            {
                offSprings[i][j] = population[selectedParents[i]][j];
                offSprings[i+1][j] = population[selectedParents[i+1]][j];
            }
        }
    }  
    
    //--------------------Mutuation----------------------
    private void mutuation()
    {
        //*** too much random needs to be created
        Random rand = new Random(System.currentTimeMillis());
        for(int i = 0; i < populationSize; i++)
            for(int j = 0; j < chromosomeLength; j++)
            {
                double t = rand.nextDouble();
                if(t < mutuationProbability)
                    population[i][j] = (byte)((byte)(population[i][j] + 1) % 2);
            }
    }
    private void elitism() throws IOException
    {
        //XXX ***check elitism if it is working fine or not***
        insertElites();
        
        double t = System.currentTimeMillis();
        evaluateAll();
        evaluateTime += System.currentTimeMillis() - t;
        
        t = System.currentTimeMillis();
        sort();
        sortTime += System.currentTimeMillis() - t;
        try (Formatter log = new Formatter(new FileWriter("generation_bests", true))) 
        {
            log.format("%f ", fitness[0]);
        }
        
        selectElites();
        
        
    }
    //*** later enhance 
    private boolean stoppingCriteria()
    {
        return false;
    }
    
    //evaluate all current population fitness
    private void evaluateAll() throws IOException
    {
        for(int i = 0; i < populationSize; i++) 
        {
            System.out.println("\n#population: " + i);
            fitness[i] = evaluate(population[i]);
        }
    }
    //sort population Descendary
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
                    fitness[j+1] = fitness[j];
                    System.arraycopy(population[j], 0, population[j+1], 0, chromosomeLength);
                }
            }
        
        population = tempPopulation;
        //evaluateAll();
    }
    //select select "eliteNum" best members of population
    private void selectElites()
    {
        elites = new byte[eliteNumber][chromosomeLength];
        ///System.arraycopy(population, 0, elites, 0, eliteNumber);
        for(int i = 0; i < eliteNumber; i++)
            for(int j = 0; j < chromosomeLength; j++)
                 elites[i][j] = population[i][j];  
    }
    //insert "eliteNum" of previous generations best members instead of worst members of current population
    private void insertElites()
    {
        for(int i = 0; i < eliteNumber; i++)
            for(int j = 0; j < chromosomeLength; j++)
                population[populationSize - eliteNumber + i][j] = elites[i][j];  
        //System.arraycopy(elites, 0, population, populationSize - eliteNumber, eliteNumber);  
    }
    
    int arrtoi(byte[] input)
    {
        int res = 0;
        
        for(int i = input.length-1, j = 0; i >= 0; i--, j++)
            res += Math.pow(j, input[i]);
        
        return res;
    }
    
} 

