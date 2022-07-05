package com.wlj.airconditionorcontrol.tools;

public class md5 {
    static private int[] State = new int[4];
    static private int[] count = new int[2];
    static private byte[] buffer = new byte[64];
    static private byte[] PADDING = new byte[]{(byte) 0x80,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    public static void MD5Update(byte[] input, int inputlen){
        int i, index, padlen;
        index = (count[0] >> 3) & 0x3f;
        padlen = 64 - index;
        count[0] += inputlen << 3;

        if(count[0] < (inputlen << 3)){
            count[1]++;
        }
        count[1] += inputlen >> 29;

        if(inputlen >= padlen){
            System.arraycopy(input, 0 ,buffer, index, padlen);
            MD5Transform(buffer, 0);
            for(i = padlen; i + 64 <= inputlen; i+= 64){
                MD5Transform(input, i);
            }
            index = 0;
        }else{
            i = 0;
        }
        System.arraycopy(input, i, buffer, index, inputlen - i);
    }


    public static byte[] MD5Final(){
        int index, padlen;
        index = (count[0] >> 3) & 0x3f;
        padlen = (index < 56)?(56 - index):(120 - index);
        byte[] bits = encode(count, 8);
        MD5Update(PADDING, padlen);
        MD5Update(bits, 8);
        return encode(State, 16);
    }

    public static void MD5Transform(byte[] block, int i){
        int a = State[0];
        int b = State[1];
        int c = State[2];
        int d = State[3];
        int [] x = decode(block, 64, i);
        /* Round 1 */
        a = FF(a, b, c, d, x[ 0], 7, 0xd76aa478); /* 1 */
        d = FF(d, a, b, c, x[ 1], 12, 0xe8c7b756); /* 2 */
        c = FF(c, d, a, b, x[ 2], 17, 0x242070db); /* 3 */
        b = FF(b, c, d, a, x[ 3], 22, 0xc1bdceee); /* 4 */
        a = FF(a, b, c, d, x[ 4], 7, 0xf57c0faf); /* 5 */
        d = FF(d, a, b, c, x[ 5], 12, 0x4787c62a); /* 6 */
        c = FF(c, d, a, b, x[ 6], 17, 0xa8304613); /* 7 */
        b = FF(b, c, d, a, x[ 7], 22, 0xfd469501); /* 8 */
        a = FF(a, b, c, d, x[ 8], 7, 0x698098d8); /* 9 */
        d = FF(d, a, b, c, x[ 9], 12, 0x8b44f7af); /* 10 */
        c = FF(c, d, a, b, x[10], 17, 0xffff5bb1); /* 11 */
        b = FF(b, c, d, a, x[11], 22, 0x895cd7be); /* 12 */
        a = FF(a, b, c, d, x[12], 7, 0x6b901122); /* 13 */
        d = FF(d, a, b, c, x[13], 12, 0xfd987193); /* 14 */
        c = FF(c, d, a, b, x[14], 17, 0xa679438e); /* 15 */
        b = FF(b, c, d, a, x[15], 22, 0x49b40821); /* 16 */

        /* Round 2 */
        a = GG(a, b, c, d, x[ 1], 5, 0xf61e2562); /* 17 */
        d = GG(d, a, b, c, x[ 6], 9, 0xc040b340); /* 18 */
        c = GG(c, d, a, b, x[11], 14, 0x265e5a51); /* 19 */
        b = GG(b, c, d, a, x[ 0], 20, 0xe9b6c7aa); /* 20 */
        a = GG(a, b, c, d, x[ 5], 5, 0xd62f105d); /* 21 */
        d = GG(d, a, b, c, x[10], 9,  0x2441453); /* 22 */
        c = GG(c, d, a, b, x[15], 14, 0xd8a1e681); /* 23 */
        b = GG(b, c, d, a, x[ 4], 20, 0xe7d3fbc8); /* 24 */
        a = GG(a, b, c, d, x[ 9], 5, 0x21e1cde6); /* 25 */
        d = GG(d, a, b, c, x[14], 9, 0xc33707d6); /* 26 */
        c = GG(c, d, a, b, x[ 3], 14, 0xf4d50d87); /* 27 */
        b = GG(b, c, d, a, x[ 8], 20, 0x455a14ed); /* 28 */
        a = GG(a, b, c, d, x[13], 5, 0xa9e3e905); /* 29 */
        d = GG(d, a, b, c, x[ 2], 9, 0xfcefa3f8); /* 30 */
        c = GG(c, d, a, b, x[ 7], 14, 0x676f02d9); /* 31 */
        b = GG(b, c, d, a, x[12], 20, 0x8d2a4c8a); /* 32 */

        /* Round 3 */
        a = HH(a, b, c, d, x[ 5], 4, 0xfffa3942); /* 33 */
        d = HH(d, a, b, c, x[ 8], 11, 0x8771f681); /* 34 */
        c = HH(c, d, a, b, x[11], 16, 0x6d9d6122); /* 35 */
        b = HH(b, c, d, a, x[14], 23, 0xfde5380c); /* 36 */
        a = HH(a, b, c, d, x[ 1], 4, 0xa4beea44); /* 37 */
        d = HH(d, a, b, c, x[ 4], 11, 0x4bdecfa9); /* 38 */
        c = HH(c, d, a, b, x[ 7], 16, 0xf6bb4b60); /* 39 */
        b = HH(b, c, d, a, x[10], 23, 0xbebfbc70); /* 40 */
        a = HH(a, b, c, d, x[13], 4, 0x289b7ec6); /* 41 */
        d = HH(d, a, b, c, x[ 0], 11, 0xeaa127fa); /* 42 */
        c = HH(c, d, a, b, x[ 3], 16, 0xd4ef3085); /* 43 */
        b = HH(b, c, d, a, x[ 6], 23,  0x4881d05); /* 44 */
        a = HH(a, b, c, d, x[ 9], 4, 0xd9d4d039); /* 45 */
        d = HH(d, a, b, c, x[12], 11, 0xe6db99e5); /* 46 */
        c = HH(c, d, a, b, x[15], 16, 0x1fa27cf8); /* 47 */
        b = HH(b, c, d, a, x[ 2], 23, 0xc4ac5665); /* 48 */

        /* Round 4 */
        a = II(a, b, c, d, x[ 0], 6, 0xf4292244); /* 49 */
        d = II(d, a, b, c, x[ 7], 10, 0x432aff97); /* 50 */
        c = II(c, d, a, b, x[14], 15, 0xab9423a7); /* 51 */
        b = II(b, c, d, a, x[ 5], 21, 0xfc93a039); /* 52 */
        a = II(a, b, c, d, x[12], 6, 0x655b59c3); /* 53 */
        d = II(d, a, b, c, x[ 3], 10, 0x8f0ccc92); /* 54 */
        c = II(c, d, a, b, x[10], 15, 0xffeff47d); /* 55 */
        b = II(b, c, d, a, x[ 1], 21, 0x85845dd1); /* 56 */
        a = II(a, b, c, d, x[ 8], 6, 0x6fa87e4f); /* 57 */
        d = II(d, a, b, c, x[15], 10, 0xfe2ce6e0); /* 58 */
        c = II(c, d, a, b, x[ 6], 15, 0xa3014314); /* 59 */
        b = II(b, c, d, a, x[13], 21, 0x4e0811a1); /* 60 */
        a = II(a, b, c, d, x[ 4], 6, 0xf7537e82); /* 61 */
        d = II(d, a, b, c, x[11], 10, 0xbd3af235); /* 62 */
        c = II(c, d, a, b, x[ 2], 15, 0x2ad7d2bb); /* 63 */
        b = II(b, c, d, a, x[ 9], 21, 0xeb86d391); /* 64 */
        long a1 = a & 0x0ffffffffL;
        long b1 = b & 0x0ffffffffL;
        long c1 = c & 0x0ffffffffL;
        long d1 = d & 0x0ffffffffL;
        long s1 = State[0] & 0x0ffffffffL;
        long s2 = State[1] & 0x0ffffffffL;
        long s3 = State[2] & 0x0ffffffffL;
        long s4 = State[3] & 0x0ffffffffL;
        State[0] = (int)(a1 + s1);
        State[1] = (int)(b1 + s2);
        State[2] = (int)(c1 + s3);
        State[3] = (int)(d1 + s4);
    }


    private static byte[] encode(int[] input, int len){
        int i = 0, j = 0;
        byte[] output = new byte[16];
        while(j < len){
            output[j] = (byte) (input[i] & 0x0ff);
            output[j+1] = (byte) ((input[i] >>> 8) & 0x0ff);
            output[j+2] = (byte) ((input[i] >>> 16) & 0x0ff);
            output[j+3] = (byte) ((input[i] >>> 24) & 0x0ff);
            i ++;
            j += 4;
        }
        return output;
    }

    private static int[] decode(byte[] input, int len, int plus){
        int i = 0, j = 0;
        int[] output = new int[64];
        while(j < len){
            output[i] = ((input[j + plus] & 0xff) |
                    ((input[j+1 + plus] << 8) & 0xff00) |
                    ((input[j+2+plus] << 16) & 0xff0000) |
                    ((input[j+3+plus] << 24)) & 0xff000000);

            i ++;
            j += 4;
        }
        return output;
    }

    public static int FF(int a, int b, int c, int d, int x, int s, int ac){

        long a1=a& 0x0ffffffffL;
        long b1=b& 0x0ffffffffL;
        long d1=d& 0x0ffffffffL;
        long c1=c& 0x0ffffffffL;
        long x1=x& 0x0ffffffffL;
        long s1=s& 0x0ffffffffL;
        long ac1=ac& 0x0ffffffffL;
        long f = F(b1,c1,d1);
        a1 += f + x1 + ac1;
        a1 = ROTATE_LEFT(a1,s1);
        a1 += b1;
        a = (int)a1;
        return a;
    }

    public static int GG(int a, int b, int c, int d, long x, int s, int ac){
        long a1=a& 0x0ffffffffL;
        long b1=b& 0x0ffffffffL;
        long d1=d& 0x0ffffffffL;
        long c1=c& 0x0ffffffffL;
        long x1=x& 0x0ffffffffL;
        long s1=s& 0x0ffffffffL;
        long ac1=ac& 0x0ffffffffL;
        long g = G(b1,c1,d1);
        a1 += g + x1 + ac1;
        a1 = ROTATE_LEFT(a1,s1);
        a1 += b1;
        a = (int)a1;
        return a;
    }

    public static int HH(int a, int b, int c, int d, long x, int s, int ac){
        long a1=a& 0x0ffffffffL;
        long b1=b& 0x0ffffffffL;
        long d1=d& 0x0ffffffffL;
        long c1=c& 0x0ffffffffL;
        long x1=x& 0x0ffffffffL;
        long s1=s& 0x0ffffffffL;
        long ac1=ac& 0x0ffffffffL;
        long h = H(b1,c1,d1);
        a1 += h + x1 + ac1;
        a1 = ROTATE_LEFT(a1,s1);
        a1 += b1;
        a = (int)a1;
        return a;
    }

    public static int II(int a, int b, int c, int d, long x, int s, int ac){
        long a1=a& 0x0ffffffffL;
        long b1=b&0x0ffffffffL;
        long d1=d& 0x0ffffffffL;
        long c1=c& 0x0ffffffffL;
        long x1=x& 0x0ffffffffL;
        long s1=s& 0x0ffffffffL;
        long ac1=ac& 0x0ffffffffL;
        long i = I(b1,c1,d1);
        a1 += i + x1 + ac1;
        a1 = ROTATE_LEFT(a1,s1);
        a1 += b1;
        a = (int)a1;
        return a;
    }

    private static long F(long x, long y, long z){
        return ((x & y) | (~x & z));
    }

    private static long G(long x, long y, long z){
        return ((x & z) | (y & ~z)) ;
    }

    private static long H(long x, long y, long z){
        return (x^y^z);
    }

    private static long I(long x, long y, long z){
        return (y ^ (x | ~z));
    }

    private static long ROTATE_LEFT(long x, long n){
        return (((int)(x) << (n)) | ((int)(x) >>> (32 - (n))));
    }

    private static void MD5Init(){
        count[0] = 0;
        count[1] = 0;
        State[0] = 0x67452301;
        State[1] = 0xEFCDAB89;
        State[2] = 0x98BADCFE;
        State[3] = 0x10325476;
    }

    public static byte[] getmd5(byte[] input){
        MD5Init();

        MD5Update(input, input.length);
        return MD5Final();
    }


}

