package friendshelp.android.com.bylianggao;

/**
 * Created by Gao on 2015/12/6.
 */
public class FFService {
    public int id;
    public String name;
    public String leftprice;
    public String rightprice;
    public int pic;

    public FFService(int id, String name, String left, String right, int flag) {
        this.id = id;
        this.name = name;
        this.leftprice = left;
        this.rightprice = right;
        this.pic = flag;
    }

    public int getId() {return id;}
    public String getName(){
        return name;
    }

    public void setPic(int i) {
        this.pic = i;
    }

    public int getPic() {
        return pic;
    }

    public String getPrice() {
        return leftprice + "#" + rightprice;
    }

    public void setPrice(String price) {
        int flag = 0;
        StringBuffer sbleft = new StringBuffer();
        StringBuffer sbright = new StringBuffer();
        for (int i = 0; i < price.length(); i++) {
            if (price.charAt(i) != '.') {
                if (flag == 0) {
                    sbleft.append(price.charAt(i));
                } else {
                    sbright.append(price.charAt(i));
                }
            } else {
                flag = 1;
            }
        }
        this.leftprice = sbleft.toString();
        this.rightprice = sbright.toString();
    }
}
