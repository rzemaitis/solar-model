/**
 * The code to simulate an N-body Solar system using Verlet time
 * integration.
 *
 * @author Rapolas Daugintis
 * @author Rokas Zemaitis
 */
 
import java.io.*;
import java.util.Scanner;
public class ParticleManyBody {
 
    /**
     *Main method of the class, which scans the particle from the
     *input file, defined by first argument of the class and outputs
     *modelled trajectory.
     *
     *argv[0] particle input file name
     *argv[1] parameter input file name
     *argv[2] output file name
     */
    public static void main (String[] argv) throws IOException {
	BufferedReader input = new BufferedReader(new FileReader(argv[0]));
	BufferedReader param = new BufferedReader(new FileReader(argv[1]));
	PrintWriter output = new PrintWriter(new FileWriter(argv[2]));
	Scanner parameters = new Scanner(param);
	Scanner in = new Scanner(input);
	Particle3D[] particleArray = new Particle3D[in.nextInt()];
	for (int i = 0; i < particleArray.length ; i++){
	    particleArray[i] = new Particle3D(in) ;
	}

	//Number of steps
	int numstep = parameters.nextInt(); 
	// Size of timestep
        double dt = parameters.nextDouble();     
        // Initial time
        double t = 0;
 
	//The start of the Verlet algorithm

	Vector3D[] forceArray = new Vector3D[particleArray.length];
	for (int i = 0; i < forceArray.length; i++){
	    forceArray[i] = new Vector3D();
		}

	Vector3D[][] forceTable = new Vector3D[particleArray.length][particleArray.length];
	for (int i = 0; i < forceTable.length; i++){
	    for (int j = 0; j < forceTable.length; j++){
		forceTable[i][j] = new Vector3D();
		}
	}

	Vector3D[] oldForceArray = new Vector3D[particleArray.length];
	for (int i = 0; i < oldForceArray.length; i++){
	    oldForceArray[i] = new Vector3D();
		}

	leapForceArray(particleArray, forceArray, forceTable);
 
        // Print the initial conditions to the files
	vmdEntry(particleArray, 1, output);
 
 
        //Loop over timesteps
        for (int i=0;i<numstep;i++){
 
             // Update the postion using current velocity
	    leapPositionArray(particleArray, forceArray, dt);
 
            // Update the force using current position
	    leapForceArray(particleArray, forceArray, forceTable);
 
            // Update the velocity based on average of current and new force
            leapVelocityVerletArray(particleArray, oldForceArray, forceArray, dt);
	    //Update the total energy using current position
	    //E = getEnergy(p);
 
	    //Check if current energy is minimum or maximum.
	    //   Emin = Math.min(Emin, E);
	    // Emax = Math.max(Emax, E);
 
	    //Update the old force
	    for(int j=0;j < particleArray.length; j++){
	     oldForceArray[j].copy(forceArray[j]);
	 }
            // Increase the time
            t = t + dt;
	    
	    // Print the current parameters to files
	    vmdEntry(particleArray, i+2, output);
        }

    //Print the maximum energy fluctuation
	//   System.out.printf("Maximum energy fluctuation: %10.7f\n", Math.abs(Emax-Emin));
	// System.out.printf("Smaller than 1E-06? %s\n", 1.0E-06> Math.abs(Emax-Emin));
 
        // Close the output file
        output.close();
    }
    
    /**
     *Static method to calculate the gravitational force on a particle
     * 
     *@param  particle1 Particle3D for which the force has to be calculated
     *@param  particle2 Particle3D instance which acts on the first particle
     *@return Vector3D which is a gravitational force on particle1   
     */
    public static Vector3D getForce(Particle3D p1, Particle3D p2, Vector3D f){
    double m1 = p1.getMass();
    double m2 = p2.getMass();
    double grav=1;
    Vector3D r = Particle3D.particleSeparation(p1,p2);
	f.copy(r.mult(-grav*m1*m2/(r.magSq()*r.mag())));
	return f;
    }
 
    /**
     *Static method to calculate the total energy of a system E=KE+PE
     *
     *@param particle Particle3D for which the energy has to be calculated
     *@return double which is the potential energy of a particle 
     */
    //DAR NEBAIGTA FUNKCIJA
    public static double leapPotentialEnergy(Particle3D bodies, double[][] potentialTable){
	double c=0;
	return c;

    }
    public static double totalEnergy(){
	double a=0;
	return a;
    }
    
    /**
     *Updates all positions of particles using the current velocities
     *
     *@param bodies an array of bodies to be integrated
     *@param oldforces old forces that acted on the bodies
     *@param forces forces that are currently acting on the bodies
     *@param dt time step for integration
     */
     public static void leapPositionArray(Particle3D[] bodies,
					  Vector3D[] forces, double dt){
	 for(int i=0;i<bodies.length;i++){
	     bodies[i].setPosition(Vector3D.addVector(bodies[i].getPosition(), Vector3D.addVector((bodies[i].getVelocity().mult(dt)),forces[i].mult(dt*dt*(1/(2*bodies[i].getMass()))))));
	 }
     }
      /**
     *Updates all velocities using the position of particles and forces by Velocity Verlet method
     *
     *@param bodies an array of bodies to be integrated
     *@param oldforces old forces that acted on the bodies
     *@param forces forces that are currently acting on the bodies
     *@param dt time step for integration
     */
     public static void leapVelocityVerletArray(Particle3D[] bodies, Vector3D[] oldForces,
     Vector3D[] forces, double dt){
	 for(int i=0;i<bodies.length;i++){
	     bodies[i].setVelocity(Vector3D.addVector(bodies[i].getVelocity(),
						      (Vector3D.addVector(oldForces[i],forces[i]).
						       mult((1/(2*bodies[i].getMass()))))));
	 }
     }
      /**
     *Updates all forces using the position of particles
     *
     *@param bodies an array of bodies
     *@param oldforces old forces that acted on the bodies
     *@param forces forces that are currently acting on the bodies
     */
     public static void leapForceArray(Particle3D[] bodies, Vector3D[] forces,Vector3D[][] forcetable ){
	 for(int i=0; i < bodies.length; i++){
	     for(int j=0;j<bodies.length;j++){
		 if(j<i || j==i){
		     continue;
		 }
		 else{
		     forcetable[i][j]=getForce(bodies[i],bodies[j],forcetable[i][j]);
		 }
	     }
	 }
	     for(int i=0; i < bodies.length; i++){
		 forces[i].setVector(0.0, 0.0 , 0.0);
	     for(int j=0;j<bodies.length;j++){
		 if(j==i){
		     continue;
		 }
		 else if(j<i){
		     Vector3D.addVector(forces[i],forcetable[j][i].mult(-1));
		 }
		 else{
		     Vector3D.addVector(forces[i],forcetable[i][j]);
		 }
	 }
	     }
     }
      /**
     *Writes out particle’s parameters in format suitable for a VMD trajectory file
     *@param b an array of bodies
     *@return Vector3D which is the total energy of a particle 
     */
public static void vmdEntry(Particle3D[] bodies, int step, PrintWriter output){
	output.printf("%d\n",bodies.length);
	output.printf("Point = %d\n",step);
	for(int i=0; i<bodies.length;i++){
	    output.printf("%s\n", bodies[i]);
	}
    }    

}
   
