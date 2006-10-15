// ============================================================================
//   Copyright 2006 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package org.uncommons.watchmaker.examples.travellingsalesman;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.uncommons.maths.PermutationGenerator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

/**
 * Naive brute-force solution to the travelling salesman problem. It would take about
 * a day and a half to brute-force the 15-city travelling salesman problem on a home
 * computer using this implementation.  However, this is a not the best possible
 * implementation that is guaranteed to find a the shortest route (for example there
 * is no branch-and-bound optimisation).
 * @author Daniel Dyer
 */
public class BruteForceTravellingSalesman implements TravellingSalesmanStrategy
{
    public String getDescription()
    {
        return "Brute Force";
    }

    public List<String> calculateShortestRoute(Collection<String> cities,
                                               ProgressListener progressListener)
    {
        // To reduce the search space we will only consider routes that start
        // and end at one city (whichever is first in the collection).  All other
        // possible routes are equivalent to one of these routes since start city
        // is irrelevant in determining the shortest cycle.
        Iterator<String> iterator = cities.iterator();
        String startCity = iterator.next();
        Collection<String> destinations = new ArrayList<String>(cities.size() - 1);
        while (iterator.hasNext())
        {
            destinations.add(iterator.next());
        }

        FitnessEvaluator<List<String>> evaluator = new RouteEvaluator();
        PermutationGenerator<String> generator = new PermutationGenerator<String>(destinations);
        long totalPermutations = generator.getTotalPermutations();
        long count = 0;
        List<String> shortestRoute = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        List<String> currentRoute = new ArrayList<String>(cities.size());
        while (generator.hasMore())
        {
            List<String> route = generator.nextPermutationAsList(currentRoute);
            route.add(0, startCity);
            double distance = evaluator.getFitness(route);
            if (distance < shortestDistance)
            {
                shortestDistance = distance;
                shortestRoute = new ArrayList<String>(route);
            }
            ++count;
            if (count % 1000 == 0)
            {
                progressListener.updateProgress(((double) count) / totalPermutations * 100);
            }
        }
        progressListener.updateProgress(100); // Finished.
        return shortestRoute;
    }
}
