package ofcprobe.vswitch.trafficgen;

import org.apache.commons.math3.distribution.AbstractRealDistribution;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Class generates random IAT values after a Set Distribution
 * @author Christopher Metter(christopher.metter@informatik.uni-wuerzburg.de)
 *
 */
public class IATGen {
	
	/**
	 * Debugger
	 */
	private static final Logger logger = LoggerFactory.getLogger(IATGen.class);
	/**
	 * The Distri
	 */
	private AbstractRealDistribution distri;
	/**
	 * The other Distri
	 */
	private IntegerDistribution intDistri;

	/**
	 * Constructor
	 * @param config the Config
	 */
	public IATGen(String distribution, double para1, double para2){
		
		logger.trace("Distribution selected: {} with Parameters {} & {}", distribution, para1, para2);
		
		switch (distribution) {
		case "ChiSquared":
			this.distri = new ChiSquaredDistribution(para1);
			break;
		case "Exponential":
			this.distri = new ExponentialDistribution(para1);
			break;
		case "Gamma":
			this.distri = new GammaDistribution(para1, para2);
			break;
		case "Poisson":
			this.intDistri = new PoissonDistribution(para1, para2);
			break;
		default:
			this.distri = new NormalDistribution(para1, para2);
			break;
		}
	}
	
	/**
	 * Gets the next IAT after set Distribution
	 * @return next IAT
	 */
	public int nextIAT(){
		if (this.distri != null) {
			double sample = this.distri.sample();
			if (sample < 0){
				sample = sample * -1;
			}
			logger.trace("New SampleValue: {}", (int)sample);
			return (int) sample;
		}
		
		int sample = this.intDistri.sample();
		if (sample < 0){
			sample = sample * -1;
		}
		logger.trace("New SampleValue: {}", sample);
		return sample;
		
	}

}
