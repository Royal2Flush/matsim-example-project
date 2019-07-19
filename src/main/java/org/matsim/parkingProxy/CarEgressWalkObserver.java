package org.matsim.parkingProxy;

import java.io.File;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.controler.events.BeforeMobsimEvent;
import org.matsim.core.controler.listener.BeforeMobsimListener;
import org.matsim.parkingProxy.penaltyCalculator.PenaltyCalculator;
import org.matsim.parkingProxy.penaltyCalculator.PenaltyFunction;
import org.matsim.parkingProxy.penaltyCalculator.PenaltyGenerator;

/**
 * Before the mobsim, gets the current {@linkplain PenaltyCalculator} from the provided {@linkplain PenaltyGenerator} and
 * resets that latter so it can - theoretically - gather information during the new iteration. However, it is your own
 * responsibility to feed it with data. The penalty for each space-time-gridcell with non-zero penalty is dumped for each
 * iteration. In the zeroth iteration, {@linkplain PenaltyCalculator#getDummyCalculator()} is used to calculate the penalties.
 * 
 * @author tkohl / Senozon
 *
 */
public class CarEgressWalkObserver implements BeforeMobsimListener {
	
	private static final String INSERTIONKEY = "[INSERTIONKEY]";
	public static final String OUTFILE_PENALTIES = "penalties_iter" + INSERTIONKEY + ".csv";
	public static final String CARMODE = TransportMode.car;	
	
	private final PenaltyGenerator penaltyGenerator;
	private final PenaltyFunction penaltyFunction;
	
	private PenaltyCalculator penaltyCalculator;
	
	/**
	 * Sets the class up with the {@linkplain PenaltyCalculator.DefaultPenaltyFunction} and the specified {@linkplain PenaltyGenerator}.
	 * 
	 * @param penaltyGenerator
	 */
	public CarEgressWalkObserver(PenaltyGenerator penaltyGenerator, PenaltyFunction penaltyFunction) {
		this.penaltyGenerator = penaltyGenerator;
		this.penaltyFunction = penaltyFunction;
	}

	@Override
	public void notifyBeforeMobsim(BeforeMobsimEvent event) {
		// update the Penalties to the result of the last iteration
		if (event.getIteration() == 0) {
			this.penaltyCalculator = PenaltyCalculator.getDummyCalculator();
		} else {
			this.penaltyCalculator = this.penaltyGenerator.generatePenaltyCalculator();
			this.penaltyCalculator.setPenaltyFunction(this.penaltyFunction);
			this.penaltyGenerator.reset();
		}
		this.penaltyCalculator.dump(new File(event.getServices().getConfig().controler().getOutputDirectory(),
				OUTFILE_PENALTIES.replace(INSERTIONKEY, Integer.toString(event.getIteration()))));
	}
	
	/*package*/ PenaltyCalculator getPenaltyCalculator() {
		return this.penaltyCalculator;
	}

}
