package org.jcryptool.visual.merkletree.algorithm;

import java.util.ArrayList;

public interface ISimpleMerkle {
	// Variablen

	public void addPrivateSeed(byte[] privateSeed);

	public void addPublicSeed(byte[] publiSeed);

	public void addTreeLeaf(byte[] LeafContent, String pubKey);

	public byte[] getMerkleRoot();

	public byte[] getPrivateSeed();

	public byte[] getPublicSeed();
	
	public int getKeyLength();

	public int getLeafCounter();

	public boolean isGenerated();

	public Node getTreeLeaf(int treeLeaveNumber);

	public byte[] getNodeContentbyIndex(int index);

	public ArrayList<Node> getTree();

	public void generateMerkleTree();

	public String sign(String message);

	public boolean verify(String message, String signature);

	// Options
	public void selectHashAlgorithmus(String hAlgo);

	public void selectOneTimeSignatureAlgorithmus(String hash, String algo);
	
	public OTS getOneTimeSignatureAlgorithmus();
}