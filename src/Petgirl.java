import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Petgirl extends JFrame  implements ActionListener, MouseListener{
     
    final int up = 0,down =1,right=2,left=3;//blockを動かしたい方向
    final int LOW=5,COLUMN=4;//行と列
    final int MAX=11;//最大のblockの数
    final int LEVEL=6;//選択可能なレベル
    Block []block = new Block [MAX];
    JLabel p1,p2,p3,p4,leve,text,bg;
    JButton []button = new JButton[MAX];    
    JButton []level = new JButton[6];
    JButton menu,retry;
    JPanel playPanel = new JPanel();//プレイ用のパネル
    JPanel selectPanel; //レベルセレクト画面
    JPanel select;//セレクトボタンを置くパネル
    ImageIcon  [][]blockIcon = new ImageIcon[8][];//block画像を入れる
    ImageIcon  [] backgroundIcon = new ImageIcon[8];//背景画像(0~3が姫,4~7が娘）
    ImageIcon  [] levelSelectIcon = new ImageIcon[12];//レベル選択画像(0~5が姫,6~11が娘)
    ImageIcon  [] exitIcon =  new ImageIcon[2];//0姫　1娘
    int target;//動かしたいボタン
    int [][]array = new int[LOW][COLUMN];//[行][列]blockが入っているか記憶する
    int [][]data = new int[MAX+1][6];//block情報を入れる(テキストファイルから読み込む） 
    int number;//ボタンの数
    int mode=1;
    int stage;//選択しているレベル
    int state=0;//状態　0レベル選択　1プレイ　2タイマ　3クリア 
    int t=0;//カウンタ
    int tx,ty;//今と前の座標差を入れる
    int x,y;//スタートした時の座標
    Timer drag,clear;
   	
    Petgirl(){
  	        
    	    drag  = new Timer(50, this);
    	    drag.setActionCommand("drag");
    	    clear  = new Timer(200 , this);
    	    clear.setActionCommand("clear");

    	  //選択画面の生成
    	    selectPanel = new JPanel();    
    	    select    = new JPanel();

    	     p1 = new JLabel();
             p2 = new JLabel();
             p3 = new JLabel();
             p4 = new JLabel();
             
             text = new JLabel("問題を選んでください");
             text.setHorizontalAlignment(JLabel.CENTER);
             leve = new JLabel("箱入り娘");
             leve.setHorizontalAlignment(JLabel.CENTER);
                  
             //block画像の生成
             blockIcon[4]    = new  ImageIcon[6];
             for(int i=0;i<6;i++)//1×1    
            	 blockIcon[4][i] = new ImageIcon("../img/"+0+"_"+i+".png");    
       
             blockIcon[5]    = new  ImageIcon[5];
             for(int i=0;i<5;i++)//1×2      
            	 blockIcon[5][i] = new ImageIcon("../img/"+1+"_"+i+".png");     
            
             blockIcon[6]    = new  ImageIcon[5];
             for(int i=0;i<5;i++)//2×1      
            	 blockIcon[6][i] = new ImageIcon("../img/"+2+"_"+i+".png");     
            
             blockIcon[7]    = new  ImageIcon[1];
             for(int i=0;i<1;i++)//2×2    
            	 blockIcon[7][i] = new ImageIcon("../img/"+3+"_"+i+".png");   
                     
        //背景    
        backgroundIcon[4] = new ImageIcon("../img/upper.png");
        backgroundIcon[5] = new ImageIcon("../img/left.png");
        backgroundIcon[6] = new ImageIcon("../img/right.png");
        backgroundIcon[7] = new ImageIcon("../img/under.png");
        
        //背景をセット
        p1.setBounds(0,0,480,80);
        p2.setBounds(0,80,80,400);
        p3.setBounds(400,80,80,400);
        p4.setBounds(0,480,480,80);
        

        exitIcon[1]=new ImageIcon("../img/bg.png");
        
        bg = new JLabel();
        bg.setBounds(0,0,320,400);
        playPanel.add(bg,-1);
        playPanel.setBounds(80,80,320,400);
        
        changeBackGround();
            
        menu = new JButton("memu");
        retry = new JButton("retry");

        menu.setVisible(false);
        retry.setVisible(false);
                
        menu.setActionCommand("menu");
        menu.addActionListener(this);
        retry.setActionCommand("retry");
        retry.addActionListener(this);
        menu.setBounds(0,560,80,40);
        retry.setBounds(400,560,80,40);
        leve.setBounds(80,560,320,40);
        this.add(menu);
        this.add(retry);
        this.add(leve);
                          
        this.setTitle("petgirl");
        this.setBounds(0,0, 480, 620);
        this.setLayout(null);
        playPanel.setLayout(null);
        
        selectPanel.setLayout(null);
        selectPanel.setBounds(80,80,320,400);  
        select.setLayout(new GridLayout(2,3)); 
        select.setBounds(0,80,320,320);
        text.setBounds(0,0,320,80);
        selectPanel.add(select);
        selectPanel.add(text);

        for(int i=0;i<LEVEL;i++)
            levelSelectIcon[i] = new ImageIcon("../img"+ "/L"+(i+1)+".png");
            
		for(int i=0;i<LEVEL;i++){	
			level[i] = new JButton(levelSelectIcon[i]);
			level[i].addActionListener(this);
			level[i].setActionCommand(""+(i+1)+"");
	        level[i].addActionListener(this);
		    select.add(level[i]);   		   
		}
        this.add(playPanel);
        this.add(selectPanel);
        this.add(p1);
        this.add(p2);
        this.add(p3);
        this.add(p4);
        this.setBounds(100, 100, 480, 620);  
        
        playPanel.setVisible(false);
        selectPanel.setVisible(true);//最初はセレクト画面を表示する
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);   
        
        creatButtonBlock();
    }
    
     public static void main(String args[]){
        new Petgirl();
    }   
  
    //レベル選択の画像を変える
    public void changeLevelSelectIcon(){
    	 for(int i=0;i<LEVEL;i++)
    		 level[i].setIcon(levelSelectIcon[i+mode*6]);    		 
    }
    //背景を変える
    public void changeBackGround(){
    		p1.setIcon(backgroundIcon[0+mode*4]);//upper
    		p2.setIcon(backgroundIcon[1+mode*4]);//left
    		p3.setIcon(backgroundIcon[2+mode*4]);//right
    		p4.setIcon(backgroundIcon[3+mode*4]);//under
    		bg.setIcon(exitIcon[mode]);
    }
    //levelの文字を変える
    public void changeLabelString(){
    	if(state==0){
    		leve.setText("箱入り娘");
    	}else if(state==1)
    	leve.setText("LEVEL"+stage);
    	else if(state==3)
    		leve.setText("LEVEL"+stage+"   CLEAR!!");
  }    
    
  //画面の変更
    public void changeCenterPanel(int n){
    	if(n==0){//プレイ画面に移る
    		playPanel.setVisible(true); 
    		for(int j=0;j<LEVEL;j++)
    	    	   level[j].setVisible(false);
    	        selectPanel.setVisible(false);          
    	}else if(n==1){//セレクト画面に移る
    		playPanel.setVisible(false);
    		for(int j=0;j<LEVEL;j++)
 	    	   level[j].setVisible(true);
 	        	selectPanel.setVisible(true);          
            }  	
    }    
  //menuとretryボタンの文字を変える
    public void changeMenuRetryText(){
    	if(state==0){
    		menu.setVisible(false);
            retry.setVisible(false);
    	}else if(state==1){
            menu.setVisible(true);
            retry.setVisible(true);
    	}
    }    
    //テキストファイルの読み込み       
    public void  readText(int [][]d,int level){
    		 try{
    			 File file;
    			 file =	 new File("../img/M"+level+".txt");

    	      FileReader filereader = new FileReader(file);

    	      int x=0;
    	      int y=0;
    	      
    	      int ch;
    	      while((ch = filereader.read()) != -1){
    	    	if(ch=='\n'){
    	        	x++;     
    	        	y=0;
    	        }
    	        else
    	        d[x][y++]=ch-48;
    	      }
    	      filereader.close();
    	    }catch(FileNotFoundException e){
    	      System.out.println(e);
    	    }catch(IOException e){
    	      System.out.println(e);
    	     }  
    		 number = data[0][0]+10;
    	  } 
    //blockとbuttonの生成
    public void creatButtonBlock(){
    	 for(int i=0;i<MAX;i++){
   	        button[i] = new JButton();
   	        block[i] = new Block();
   	        button[i].setActionCommand(""+i+"");  
 	        button[i].addMouseListener(this);//ドラッグで操作できるようにする
 	        
     	  }
    }
    //blockの情報を更新する
    public void changeBlock(){   
      for(int i=0;i<number;i++){
    	  block[i].setData(data[i+1]);;    
      }
  }
  	//buttonの情報を更新する
    public void changeButton(){
	  for(int i=0;i<number;i++){
	        button[i].setIcon(blockIcon[block[i].type+mode*4][+block[i].number]);
	        button[i].setBounds(block[i].column*80,block[i].low*80, block[i].width*80,block[i].height*80);  
	        playPanel.add(button[i],0);	        
	        }
}
  //ボタンがある場所を配列に登録する
    public void setArray(Block  []b){
        for(int i=0;i<number;i++)
            for(int j=b[i].low;j<b[i].low+b[i].height;j++)
                for(int k=b[i].column;k<b[i].column+b[i].width;k++){
                    array[j][k]=1;
                }
    }
  	//配列をすべて0にする
    public  void resetArray(){
	  for(int i=0;i<LOW;i++)
		  for(int j=0;j<COLUMN;j++)
			  array[i][j]=0;
  }
  	//buttonを削除する
    public void deleteButton(){
    	for(int i=0;i<number;i++) 	 
    		playPanel.remove(button[i]);   		  
    	playPanel.repaint();
  }
    //ボタンを動かせるか調べる
    public boolean nextCheck(Block b,int direction){
        switch(direction){
        case up:
        	for(int i=0;i<b.width;i++)
            	try{ 
            		if(array[b.low-1][b.column+i]==1)
                 return false;
        	}catch(ArrayIndexOutOfBoundsException e){
        		return false;
        	}
            break;
        case down:        	
            for(int i=0;i<b.width;i++)
            	try{ 
                if(array[b.low+b.height][b.column+i]==1)
                    return false;
        }catch(ArrayIndexOutOfBoundsException e){
    		return false;
    	}
            break;                  
        case right:
        	try{
            for(int i=0;i<b.height;i++)
                if(array[b.low+i][b.column+b.width]==1)
                    return false;
        }catch(ArrayIndexOutOfBoundsException e){
    		return false;
    	}
            break;
        case left:
        	try{
        	 for(int i=0;i<b.height;i++)
                if(array[b.low+i][b.column-1]==1)
                    return false;
        }catch(ArrayIndexOutOfBoundsException e){
    		return false;
    	}
        break;
        default: return false;
    }
        return true;                            
    } 
  	//ボタンの移動があった時ボタンの情報を書き換える
    public void changeData(Block  b,int direction){
        if(direction==up)
            b.low-=1;
        else if(direction==down)
            b.low+=1;
        else if(direction==right)
            b.column+=1;
        else if(direction==left)
            b.column-=1;  
        if(target==0 && block[0].low==3 && block[0].column==1){
        	 state=2; //クリアタイマ状態
        	 drag.stop();
        	 clear.start();      	
        }       	
    }        
   //移動後に必要になった配列を埋める
    public void changeArray(Block b,int direction){
    	 switch(direction){
         case up:{
            for(int i=0;i<b.width;i++)
                array[b.low-1][b.column+i]=1;
            for(int i=0;i<b.width;i++)
                array[b.low+(b.height-1)][b.column+i]=0;
            break;
         }case down:{
            for(int i=0;i<b.width;i++)
                array[b.low+b.height][b.column+i]=1;
            for(int i=0;i<b.width;i++)
                array[b.low][b.column+i]=0;
            break;
         }case right:{
            for(int i=0;i<b.height;i++)
                array[b.low+i][b.column+b.width]=1;
            for(int i=0;i<b.height;i++)
                array[b.low+i][b.column]=0;
            break;
    	 }case left:{
            for(int i=0;i<b.height;i++)
                array[b.low+i][b.column-1]=1; 
            for(int i=0;i<b.height;i++)
                array[b.low+i][b.column+(b.width-1)]=0; 
            break;
    	 }
    }
    } 
    //ボタンの座標変更
    public void changeCoordinate(int target){
         button[target].setBounds(block[target].column*80,block[target].low*80, block[target].width*80,block[target].height*80);
    }         
    //ゲームをやり直す時に使う
    public void reset(){
      deleteButton();//今あるボタンを削除する
      readText(data,stage);    //新しいデータを読み込む
      changeBlock();   //blockをの情報の更新
  	  resetArray();//配列をすべて0にする
  	  setArray(block);    //blockのある場所を登録する
  	  changeButton(); //buttonの情報の更新
      leve.setText("LEVEL"+stage);
    }  
  //モード変更
    public void changeMode(int m){
    	if(m==mode)//現在と同じモードを選んだら何もしない
    		return;
    	else{
    		mode=m;
    		changeLabelString();
			changeLevelSelectIcon();
			changeBackGround();
    	}
    }
    //レベル選択画面に戻る
    public void menu(){
    	 state=0;
		 changeLabelString();
		 changeCenterPanel(1); 
		 changeMenuRetryText();	  
    }    
  //ドラッグされた時
    public void mousePressed(MouseEvent e) {
    	if(state==1){   	 
    		Object b = e.getSource();//押されたボタンを調べる   	 
    		String command = ((JButton)b).getActionCommand();
    		int i = Integer.parseInt(command);  
    		target = i;//押されたボタンを記憶する
   	
    		PointerInfo pi=MouseInfo.getPointerInfo();
    		java.awt.Point pp=pi.getLocation();
    		x=pp.x; //ドラッグした時の座標
    		y=pp.y;
    		drag.start();   
    	}
	 }
    //ドラッグが終わった時
    public void mouseReleased(MouseEvent e) {	
    drag.stop();
   } 
    //ボタンが押されたら呼び出される
    public void actionPerformed(ActionEvent e){
    	 String es=e.getActionCommand();
	      if(es.equals("drag")){
    		 PointerInfo pi=MouseInfo.getPointerInfo();
 	    	 java.awt.Point pp=pi.getLocation();
 	    	  int tmp=-1;
 	    	    tx=pp.x-x;//今引く前
 	            ty=pp.y-y;
 	      
 	       if( Math.abs(tx)>10 || Math.abs(ty)>10){//動かせる    	   
 	           if( Math.abs(tx) >= Math.abs(ty)){//左右どちらかに動く時
 	           if(tx>10)
 	        	   tmp=right;
 	        	else if(tx<-10)
 	        	  tmp=left;	 	           
 	           }else {//上下どちらかに動く時
 	        	   if(ty>10)
 	        	   tmp=down;
 	        	  else  if(ty<-10)
 	        	   tmp=up;	        	   
 	           }	         
	          if(nextCheck(block[target],tmp)){//動かせる時	        	  
	        	  	changeArray(block[target],tmp);//配列の情報の変更	        	 
	        	  	changeData(block[target],tmp);//移動したBlockの情報の変更
	        	  	changeCoordinate(target);//再描写
	        	        }
	      }else ;//動きが小さすぎて動かせない	   
 	       //座標の更新
 	           x=pp.x; 
	           y=pp.y;  	           
    	 }else if(es.equals("clear")){//クリアタイマ
     		if(t==0){//初めの一回だけの処理
        		playPanel.remove(button[0]);   		
        		this.add(button[0],0);
        		}
     				if (t >= 30){
        		      clear.stop();
        		      t=0;
        		      this.remove(button[0]);
        		      state=3;//クリア状態にする
        		      changeLabelString();
        		      changeMenuRetryText();
        		     repaint();
        		    }else{
        		      t++;
        		      button[0].setBounds(160,320+t*10,160,160);     		           	          
        		     }		
    	 }else if(state==0){//レベル選択画面
    		  if(es.equals("menu")){   			  
    			  changeMode(0);   			  
    			  }else if(es.equals("retry")){
    				  changeMode(1);  		
    		  }else {
    			  int i = Integer.parseInt(es);
    			  changeCenterPanel(0); 	        		
    	          stage = i;
    	          state=1;//プレイ状態になる
    	          changeLabelString();
    	          changeMenuRetryText();
    	          reset();     
    		  }
    	         }else if(es.equals("menu")){//menuが押された
	        		 menu(); 
	        	 }else if(es.equals("retry")){//retryが押された
	        		 reset();        		 
	        	 }  	 
 }         
    public void mouseClicked(MouseEvent e){}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}


class Block {
        int height;//高さ
        int width; //幅
        int low;   //行
        int column;//列
        int type;// 0(1×1) 1(1×2) 2(2×1) 3(2×2)
        int number;//同じ種類の画像の何番目
        
        void setData(int []data){
            low =data[0];
            column=data[1];
            width=data[2];
            height=data[3];
            type=data[4];
            number=data[5];         
        }       
    }

