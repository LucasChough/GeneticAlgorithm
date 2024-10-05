import java.util.Random;

public class Chromosome {
    private static final int CHROMOSOME_LENGTH = 50; // Length of the chromosome
    private String genes; // String of genes
    private static Random random = new Random();

    // Constructor: Creates a random chromosome
    public Chromosome() {
        StringBuilder sb = new StringBuilder(CHROMOSOME_LENGTH);
        for (int i = 0; i < CHROMOSOME_LENGTH; i++) {
            sb.append(random.nextBoolean() ? '1' : '0'); // Randomly initialize each gene
        }
        genes = sb.toString();
    }

    // Constructor: Creates a chromosome with a specific string of genes
    public Chromosome(String genes) {
        if (genes.length() != CHROMOSOME_LENGTH) {
            throw new IllegalArgumentException("Chromosome must have " + CHROMOSOME_LENGTH + " genes.");
        }
        this.genes = genes; // Directly assign the string
    }

    // Get the genes of the chromosome
    public String getGenes() {
        return genes; // Return the string directly
    }

    // Calculate fitness (number of 1's in the chromosome)
    public int calculateFitness() {
        int fitness = 0;
        for (char gene : genes.toCharArray()) {
            if (gene == '1') {
                fitness++;
            }
        }
        return fitness;
    }

    // Perform crossover with another chromosome
    public Chromosome crossover(Chromosome other, double Pc) {
        if (CHROMOSOME_LENGTH <= 1) {
            throw new IllegalStateException("Chromosome length must be greater than 1 for crossover.");
        }
        StringBuilder newGenes = new StringBuilder(CHROMOSOME_LENGTH);

        if (random.nextDouble() <= Pc) {
            int crossoverPoint = random.nextInt(CHROMOSOME_LENGTH); // Random crossover point
            for (int i = 0; i < crossoverPoint; i++) {
                newGenes.append(this.genes.charAt(i));
            }
            for (int i = crossoverPoint; i < CHROMOSOME_LENGTH; i++) {
                newGenes.append(other.genes.charAt(i));
            }
        } else {
            newGenes.append(this.genes); // No crossover, return a copy of the current chromosome
        }

        return new Chromosome(newGenes.toString());
    }

    // Perform mutation based on mutation probability Pm
    public void mutate(double Pm) {
        if (Pm < 0 || Pm > 1) {
            throw new IllegalArgumentException("Mutation probability (Pm) must be between 0 and 1.");
        }
        StringBuilder newGenes = new StringBuilder(genes);
        for (int i = 0; i < CHROMOSOME_LENGTH; i++) {
            if (random.nextDouble() <= Pm) {
                newGenes.setCharAt(i, genes.charAt(i) == '0' ? '1' : '0');
            }
        }
        genes = newGenes.toString();
    }

    // Print the chromosome as a string of 1's and 0's
    @Override
    public String toString() {
        return genes;
    }
}