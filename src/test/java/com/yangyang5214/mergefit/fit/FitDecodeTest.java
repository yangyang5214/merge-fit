package com.yangyang5214.mergefit.fit;


import org.junit.Test;

public class FitDecodeTest {


    @Test
    public void EncodeFit() {
        FitDecode decode = new FitDecode();
        String p = "/Users/beer/beer/merge-fit/fits/1.fit";
        decode.Encode(p);
    }
}