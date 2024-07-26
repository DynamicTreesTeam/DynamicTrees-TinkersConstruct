package maxhyper.dttconstruct.cellkits;

import com.ferreusveritas.dynamictrees.DynamicTrees;
import com.ferreusveritas.dynamictrees.api.cell.Cell;
import com.ferreusveritas.dynamictrees.api.cell.CellKit;
import com.ferreusveritas.dynamictrees.api.cell.CellNull;
import com.ferreusveritas.dynamictrees.api.cell.CellSolver;
import com.ferreusveritas.dynamictrees.api.registry.Registry;
import com.ferreusveritas.dynamictrees.cell.*;
import com.ferreusveritas.dynamictrees.util.SimpleVoxmap;

public class DTCCellKits {

    public static void register(Registry<CellKit> registry) {
        registry.registerAll(SLIME);
    }

    public static final CellKit SLIME = new CellKit(DynamicTrees.location("slime")) {
        private final Cell coniferBranch = new ConiferBranchCell();
        private final Cell[] coniferLeafCells;
        private final CellKits.BasicSolver coniferSolver;

        {
            this.coniferLeafCells = new Cell[]{CellNull.NULL_CELL, new ConiferLeafCell(1), new ConiferLeafCell(2), new ConiferLeafCell(3), new ConiferLeafCell(4), new ConiferLeafCell(5), new ConiferLeafCell(6), new ConiferLeafCell(7)};
            this.coniferSolver = new CellKits.BasicSolver(new short[]{1300, 1043, 786, 529});
        }

        public Cell getCellForLeaves(int hydro) {
            return this.coniferLeafCells[hydro];
        }

        public Cell getCellForBranch(int radius, int meta) {
            return radius == 1 ? this.coniferBranch : CellNull.NULL_CELL;
        }

        public SimpleVoxmap getLeafCluster() {
            return LeafClusters.CONIFER;
        }

        public CellSolver getCellSolver() {
            return this.coniferSolver;
        }

        public int getDefaultHydration() {
            return 4;
        }
    };

}
