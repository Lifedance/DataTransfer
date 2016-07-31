package com.xtonic.task.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xtonic.config.SrcData;
import com.xtonic.task.AbstractTransferTask;

public class InsertTask extends AbstractTransferTask {

	public List getDataFromSrc(List srcDatas, int pageNo, int pageSize) {
		SrcData srcData = (SrcData) srcDatas.get(0);
		Connection conn = config.getDataSources(srcData.getSrcDataSourceRef()).getConnection();
		/*String sql = "INSERT INTO `testA`.`TEST_TABLE_6` (`id`,`c1`,`c2`,`c3`,`c4`,`c5`,`c6`," + "`c7`,`c8`,`c9`,"
				+ "`c10`,`11`,`c12`,`c13`,`c14`,`c15`,`c16`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";*/
		
		//String sql ="INSERT INTO src5User (id,inttrested,memo) VALUES (?,?,?)"; 
		
		String sql ="INSERT INTO src3User (id,name,age) VALUES (?,?,?)"; 
		
		
		PreparedStatement pst = null;
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			for(int i = 1; i <500001; i ++){
				pst.setInt(1, i);
				/*pst.setString(2, "TEST_TABLE_1_c1_"+(i+1));
				pst.setString(3, "TEST_TABLE_1_c2_"+(i+1));
				pst.setString(4, "TEST_TABLE_1_c3_"+(i+1));
				pst.setString(5, "TEST_TABLE_1_c4_"+(i+1));
				pst.setString(6, "TEST_TABLE_1_c5_"+(i+1));
				pst.setString(7, "TEST_TABLE_1_c6_"+(i+1));
				pst.setString(8, "TEST_TABLE_1_c7_"+(i+1));
				pst.setString(9, "TEST_TABLE_1_c8_"+(i+1));
				pst.setString(10, "TEST_TABLE_1_c9_"+(i+1));
				pst.setString(11, "TEST_TABLE_1_c10_"+(i+1));
				pst.setString(12, "TEST_TABLE_1_c11_"+(i+1));
				pst.setString(13, "TEST_TABLE_1_c12_"+(i+1));
				pst.setString(14, "TEST_TABLE_1_c13_"+(i+1));
				pst.setString(15, "TEST_TABLE_1_c14_"+(i+1));
				pst.setString(16, "TEST_TABLE_1_c15_"+(i+1));
				pst.setString(17, "TEST_TABLE_1_c16_"+(i+1));*/
				pst.setString(2, "text_mix_name"+i);
				pst.setString(3, i+"");
				pst.addBatch();
				if(i != 0  &&i % 100000 ==  0){
					pst.executeBatch();
					pst.clearBatch();
					conn.commit();
				}
				System.out.println("插入"+i+"条记录");
			}
			pst.executeBatch();
			pst.clearBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<Map<String, Object>>();
	}

}
