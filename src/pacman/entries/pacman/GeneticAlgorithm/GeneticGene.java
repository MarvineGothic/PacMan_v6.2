package pacman.entries.pacman.GeneticAlgorithm;

import java.io.Serializable;
import java.math.BigInteger;

public class GeneticGene implements Serializable {

    private int _bits;
    private double _lower, _upper;

    public GeneticGene(int bits, double lower, double upper) {
        _bits = bits;
        _lower = lower;
        _upper = upper;
    }

    public int getBits() {
        return _bits;
    }


    /**
     * Encoding a PhenoType, which is a double weight to GenoType
     *
     * @param val
     * @return
     */
    public String encode(double val) {
        double norm = (val - _lower) / (_upper - _lower);
        BigInteger bin = BigInteger.valueOf(Math.round(norm * (Math.pow(2, _bits) - 1)));
        StringBuilder bits = new StringBuilder(bin.toString(2));

        while (bits.length() < _bits)
            bits.insert(0, "0");

        return bits.toString();
    }

    /**
     * Decoding a GenoType (a bit string) to PhenoType (a double weight)
     *
     * @param bitstring
     * @return
     */
    public double decode(String bitstring) {
        double bin = new BigInteger(bitstring, 2).doubleValue();
        return _lower + (_upper - _lower) * bin / (Math.pow(2, _bits) - 1);
    }
}
