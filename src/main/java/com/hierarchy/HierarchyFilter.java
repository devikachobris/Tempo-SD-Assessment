package com.hierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;

/**
 * Provides utility methods for filtering {@link Hierarchy} objects.
 *
 * <p>Filtering rules:
 * <ul>
 *   <li>A node is included in the filtered hierarchy if its nodeId passes the predicate
 *       <b>and all its ancestors also pass the predicate</b>.</li>
 *   <li>Depths in the filtered hierarchy are recomputed to preserve valid tree structure.</li>
 * </ul>
 *
 * <p><b>Assumptions:</b>
 * <ul>
 *   <li>The input hierarchy respects DFS and depth invariants:
 *       <ul>
 *           <li>First node depth = 0.</li>
 *           <li>Depth increases by at most 1 when moving to a child.</li>
 *       </ul>
 *   </li>
 *   <li>Children always appear after parents (DFS order).</li>
 *   <li>The hierarchy may contain multiple roots (a forest).</li>
 *   <li>The predicate is side effect free and depends only on nodeId.</li>
 *   <li>Depths are bounded by the number of nodes (safe for array indexing).</li>
 *   <li>Behavior is undefined if the input hierarchy is malformed.</li>
 * </ul>
 */
class HierarchyFilter {

    /**
     * Filters the given hierarchy according to the provided predicate.
     *
     * @param hierarchy the hierarchy to filter
     * @param nodeIdPredicate the predicate to apply on node IDs
     * @return a new {@link Hierarchy} containing only nodes that satisfy the predicate
     *         along with their ancestors, preserving DFS order
     */
    public static Hierarchy filter(Hierarchy hierarchy, IntPredicate nodeIdPredicate) {
        int n = hierarchy.size();

        /* Output buffers for node IDs and depths */
        List<Integer> outputNodeIds = new ArrayList<>();
        List<Integer> outputDepths = new ArrayList<>();

        /* Tracks whether each depth level is valid (ancestors passed predicate) */
        boolean[] ancestorValidByDepth = new boolean[n];
        /* Tracks filtered depth of the last valid node at each depth */
        int[] filteredDepthByDepth = new int[n];

        for (int i = 0; i < n; i++) {
            int nodeId = hierarchy.nodeId(i);
            int depth = hierarchy.depth(i);

            /* A node is valid only if:
             1) it passes the predicate
             2) its parent (depth - 1) is valid, or it is a root */
            boolean ancestorsValid = depth == 0 || ancestorValidByDepth[depth - 1];
            boolean currentValid = ancestorsValid && nodeIdPredicate.test(nodeId);

            ancestorValidByDepth[depth] = currentValid;

            if (currentValid) {
                int filteredDepth = depth == 0 ? 0 : filteredDepthByDepth[depth - 1] + 1;
                filteredDepthByDepth[depth] = filteredDepth;

                outputNodeIds.add(nodeId);
                outputDepths.add(filteredDepth);
            }
        }

        /* Convert lists to arrays for ArrayBasedHierarchy */
        int[] nodeIdsArr = outputNodeIds.stream().mapToInt(Integer::intValue).toArray();
        int[] depthsArr = outputDepths.stream().mapToInt(Integer::intValue).toArray();

        return new ArrayBasedHierarchy(nodeIdsArr, depthsArr);
    }
}
