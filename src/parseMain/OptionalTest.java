/**
 * Copyright(c) Guangzhou JiaxinCloud Science & Technology Ltd. 
 */
package parseMain;

import java.io.File;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RemoteRefUpdate;

/**
 * <pre>
 * 程序的中文名称。
 * </pre>
 * 
 * @author 王文辉 wangwenhui@jiaxincloud.com
 * @version 1.00.00
 * @date 2017年8月29日
 * 
 *       <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 *       </pre>
 */
public class OptionalTest {

	public static void main(String[] args) throws Exception {
		File f = new File("D:\\project\\dom4j_test");
		Git git=Git.open(f);
       //git status
		Status status = git.status().call();
		Set<String> m = status.getModified();
		Set<String> u = status.getUntracked();
		System.out.println("list modified");
		for (String s : m) {
			System.out.println(s);
		}
		System.out.println("");
		System.out.println("list untracked");
		for (String s : u) {
			System.out.println(s);
		}
		
		//git add
		DirCache dirCache = git.add().addFilepattern(".").call();
		RevCommit commit = git.commit().setMessage( "use java git client commit " ).call();
		
	    Iterable<PushResult> iterable = git.push().call();
	    
	    RemoteRefUpdate remoteUpdate = iterable.iterator().next().getRemoteUpdate( "refs/heads/master" );
	    System.out.println("remoteUpdate.getStatus()->"+remoteUpdate.getStatus());
	    System.out.println("new ObjectId->"+ remoteUpdate.getNewObjectId());
//		boolean lock=dirCache.lock();
//		if(!lock){
//			dirCache.lock();
//		}
//		dirCache.commit();
//		dirCache.unlock();
	

	}

}
