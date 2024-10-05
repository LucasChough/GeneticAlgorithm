import java.util.*;
import java.io.*;

public class GeneticAlgorithm {
    private List<Chromosome> population; // List to hold the population
    public static int generation = 1;
    private Chromosome bestChromosomeOverall; // Track the best chromosome across all generations

    // Initialize population
    public void initializePopulation(int populationSize) {
        population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            Chromosome chromosome = new Chromosome(); // Randomly generate a chromosome
            population.add(chromosome); // Add chromosome to the population list
        }
    }

    public int getMinFitness() {
        int minFitness = Integer.MAX_VALUE; // Start with the highest possible value
        for (Chromosome chromosome : population) {
            int fitness = chromosome.calculateFitness();
            if (fitness < minFitness) {
                minFitness = fitness;
            }
        }
        return minFitness; // Return the lowest fitness found
    }

    public int getMaxFitness() {
        int maxFitness = Integer.MIN_VALUE; // Start with the lowest possible value
        for (Chromosome chromosome : population) {
            int fitness = chromosome.calculateFitness();
            if (fitness > maxFitness) {
                maxFitness = fitness;
            }
        }
        return maxFitness; // Return the highest fitness found
    }

    public double getAvgFitness() {
        double totalFitness = 0;
        for (Chromosome chromosome : population) {
            totalFitness += chromosome.calculateFitness(); // Sum up fitness values
        }
        return totalFitness / population.size(); // Return the average fitness
    }

    // Method to write fitness statistics to a CSV file
    public void logFitnessToCSV(int generation, int minFitness, double avgFitness, int maxFitness, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            // Format: generation number, min fitness, avg fitness, max fitness
            writer.write(generation + "," + minFitness + "," + avgFitness + "," + maxFitness);
            writer.newLine(); // Move to the next line
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // Method for Fitness Proportional Selection (Roulette Wheel Selection)
    public Chromosome fitnessProportionalSelection() {
        int totalFitness = 0;
        for (Chromosome chromosome : population) {
            totalFitness += chromosome.calculateFitness();
        }

        Random rand = new Random();
        int randomFitnessPoint = rand.nextInt(totalFitness);

        int cumulativeFitness = 0;
        for (Chromosome chromosome : population) {
            cumulativeFitness += chromosome.calculateFitness();
            if (cumulativeFitness >= randomFitnessPoint) {
                return chromosome;
            }
        }

        return population.get(population.size() - 1); // Fallback
    }

    // Tournament Selection
    public Chromosome tournamentSelection() {
        Random rand = new Random();
        Chromosome parent1 = population.get(rand.nextInt(population.size()));
        Chromosome parent2 = population.get(rand.nextInt(population.size()));
        return parent1.calculateFitness() > parent2.calculateFitness() ? parent1 : parent2;
    }

    // Method to add a new chromosome to the population
    public void addChromosome(Chromosome chromosome) {
        population.add(chromosome);
    }

    public static void main(String[] args) {
        System.out.println("Aiden Colberg, Ayden Dillon, Lucas Chough, Antonio Caporossi");

        // Ensure there are exactly four command-line arguments
        if (args.length != 4) {
            System.out.println("Usage: java GeneticAlgorithm <population size> <selection method> <Pc> <Pm>");
            System.exit(1);
        }

        try {
            // Parse command-line arguments
            int populationSize = Integer.parseInt(args[0]);
            int selectionMethod = Integer.parseInt(args[1]);
            double Pc = Double.parseDouble(args[2]); // Crossover probability
            double Pm = Double.parseDouble(args[3]); // Mutation probability

            // Input validation
            if (populationSize <= 0) {
                throw new IllegalArgumentException("Population size must be greater than 0.");
            }
            if (selectionMethod != 1 && selectionMethod != 2) {
                throw new IllegalArgumentException(
                        "Selection method must be 1 (Fitness Proportional) or 2 (Tournament).");
            }
            if (Pc < 0 || Pc > 1) {
                throw new IllegalArgumentException("Crossover probability (Pc) must be between 0 and 1.");
            }
            if (Pm < 0 || Pm > 1) {
                throw new IllegalArgumentException("Mutation probability (Pm) must be between 0 and 1.");
            }

            System.out.println("Population Size: " + populationSize);
            System.out.println("Selection Method: " + (selectionMethod == 1 ? "Fitness Proportional" : "Tournament"));
            System.out.println("Crossover Probability (Pc): " + Pc);
            System.out.println("Mutation Probability (Pm): " + Pm);

            boolean found = false;
            GeneticAlgorithm ga = new GeneticAlgorithm();
            ga.initializePopulation(populationSize);
            
            // Keep track of the best chromosome across generations
            Chromosome bestChromosomeOverall = null;

            while (!found) {
                List<Chromosome> newPopulation = new ArrayList<>();
            
                // Apply elitism: keep the best solution
                Chromosome bestChromosome = ga.population.stream()
                        .max(Comparator.comparingInt(Chromosome::calculateFitness))
                        .orElseThrow(NoSuchElementException::new);
                newPopulation.add(bestChromosome);
            
                // Generate new population
                while (newPopulation.size() < populationSize) {
                    Chromosome parent1;
                    Chromosome parent2;
            
                    // Choose the selection method
                    if (selectionMethod == 1) {
                        parent1 = ga.fitnessProportionalSelection();
                        parent2 = ga.fitnessProportionalSelection();
                    } else {
                        parent1 = ga.tournamentSelection();
                        parent2 = ga.tournamentSelection();
                    }
            
                    // Crossover
                    Chromosome offspring1 = parent1.crossover(parent2, Pc);
                    Chromosome offspring2 = parent2.crossover(parent1, Pc);
            
                    // Mutation
                    offspring1.mutate(Pm);
                    offspring2.mutate(Pm);
            
                    // Add offspring to the new population
                    newPopulation.add(offspring1);
                    newPopulation.add(offspring2);
                }
            
                ga.population = newPopulation;
            
                // Print the best chromosome of the current generation
                System.out.println("Generation " + generation + " Best Chromosome: " + bestChromosome);
            
                // Calculate fitness
                int minFitness = ga.getMinFitness();
                int maxFitness = ga.getMaxFitness();
                double avgFitness = ga.getAvgFitness();
            
                // Log to CSV
                ga.logFitnessToCSV(generation, minFitness,
                        Double.parseDouble(String.format("%.2f", avgFitness)), maxFitness, "fitness_log.csv");
            
                // Increment generation counter
                generation++;
            
                if (maxFitness == 50) {
                    found = true;
                    // Print the final best chromosome before exiting the loop
                    System.out.println("Generation " + (generation - 1) + " Best Chromosome: " + bestChromosome);
                }
            }

            // After the loop, print the last best chromosome
            Chromosome bestChromosome = ga.population.stream()
            .max(Comparator.comparingInt(Chromosome::calculateFitness))
            .orElseThrow(NoSuchElementException::new);

            // Print the best chromosome after the loop exits
            System.out.println("Generation " + generation + " Best Chromosome: " + bestChromosome);

            // Then print the solution found message
            System.out.println("Solution found in generation " + generation);

        } catch (NumberFormatException e) {
            System.out.println(
                    "Invalid input. Please ensure that population size is an integer, and Pc and Pm are valid decimal numbers.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
