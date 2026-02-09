package com.hierarchy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HierarchyFilterTest {
    @Test
    void testFilter() {
        Hierarchy unfiltered = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11},
                new int[]{0, 1, 2, 3, 1, 0, 1, 0, 1, 1, 2}
        );
        Hierarchy filteredActual = HierarchyFilter.filter(unfiltered, nodeId -> nodeId % 3 != 0);
        Hierarchy filteredExpected = new ArrayBasedHierarchy(
                new int[]{1, 2, 5, 8, 10, 11},
                new int[]{0, 1, 1, 0, 1, 2}
        );
        assertEquals(filteredExpected.formatString(), filteredActual.formatString());
    }

    @Test
    void whenParentDoesNotMatchPredicate_thenChildIsExcluded() {
        //Arrange
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{1, 2},
                new int[]{0, 1}
        );
        //Act
        Hierarchy filtered = HierarchyFilter.filter(hierarchy, id -> id == 2);
        //Assert
        assertEquals("[]", filtered.formatString());
    }

    @Test
    void whenAncestorIsFilteredOut_thenDescendantDepthsAreRecomputed() {
        //Arrange
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4},
                new int[]{0, 1, 2, 3}
        );
        //Act
        Hierarchy filtered = HierarchyFilter.filter(hierarchy, id -> id != 2);
        //Assert
        assertEquals("[1:0]", filtered.formatString());
    }

    @Test
    void whenAllNodesMatchPredicate_thenHierarchyRemainsUnchanged() {
        //Arrange
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{1, 2, 3},
                new int[]{0, 1, 2}
        );
        //Act
        Hierarchy filtered = HierarchyFilter.filter(hierarchy, id -> true);
        //Assert
        assertEquals(hierarchy.formatString(), filtered.formatString());
    }

    @Test
    void whenHierarchyContainsMultipleRoots_thenEachRootIsFilteredIndependently() {
        //Arrange
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4},
                new int[]{0, 1, 0, 1}
        );
        //Act
        Hierarchy filtered = HierarchyFilter.filter(hierarchy, id -> id != 2);
        //Assert
        assertEquals("[1:0, 3:0, 4:1]", filtered.formatString());
    }

    @Test
    void whenOneSiblingIsFilteredOut_thenOtherSiblingsRemainUnaffected() {
        //Arrange
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{1, 2, 3},
                new int[]{0, 1, 1}
        );
        //Act
        Hierarchy filtered = HierarchyFilter.filter(hierarchy, id -> id != 2);
        //Assert
        assertEquals("[1:0, 3:1]", filtered.formatString());
    }

    @Test
    void whenFilteringEndsAtOneRoot_thenNextRootStartsWithCleanState() {
        //Arrange
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{1, 2, 3, 4},
                new int[]{0, 1, 2, 0}
        );
        //Act
        Hierarchy filtered = HierarchyFilter.filter(hierarchy, id -> id != 2);
        //Assert
        assertEquals("[1:0, 4:0]", filtered.formatString());
    }

    @Test
    void whenHierarchyIsEmpty_thenFilteredHierarchyIsEmpty() {
        //Arrange
        Hierarchy hierarchy = new ArrayBasedHierarchy(new int[]{}, new int[]{});
        //Act
        Hierarchy filtered = HierarchyFilter.filter(hierarchy, id -> true);
        //Assert
        assertEquals("[]", filtered.formatString());
    }

    @Test
    void whenSingleRootMatchesPredicate_thenItIsIncludedWithDepthZero() {
        //Arrange
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{42},
                new int[]{0}
        );
        //Act
        Hierarchy filtered = HierarchyFilter.filter(hierarchy, id -> true);
        //Assert
        assertEquals("[42:0]", filtered.formatString());
    }

    @Test
    void whenSingleRootDoesNotMatchPredicate_thenHierarchyIsEmpty() {
        //Arrange
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{42},
                new int[]{0}
        );
        //Act
        Hierarchy filtered = HierarchyFilter.filter(hierarchy, id -> false);
        //Assert
        assertEquals("[]", filtered.formatString());
    }

    @Test
    void whenNoNodesMatchPredicate_thenResultIsEmptyHierarchy() {
        //Arrange
        Hierarchy hierarchy = new ArrayBasedHierarchy(
                new int[]{1, 2, 3},
                new int[]{0, 1, 1}
        );
        //Act
        Hierarchy filtered = HierarchyFilter.filter(hierarchy, id -> false);
        //Assert
        assertEquals("[]", filtered.formatString());
    }
}
