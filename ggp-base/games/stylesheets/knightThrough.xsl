
		<style type="text/css" media="all">
			#cellL
			{
				width:  46px;
				height: 46px;
				float:	left;
				border: 2px solid #FFC;
				background-color: #CCCCCC;
			}
			#cellD
			{
				width:  46px;
				height: 46px;
				float:	left;
				border: 2px solid #FFC;
				background-color: #AAAAAA;
			}
		</style>

		<!-- Draw Board -->
		<div id="main" style="position:relative; width:400px; height:400px">
			<div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/>
			<div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/>
			<div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/>
			<div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/>
			<div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/>
			<div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/>
			<div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/>
			<div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/><div id="cellD"/><div id="cellL"/>

			<!-- Draw Marks -->
			<xsl:for-each select="fact[relation='cell']">
				<xsl:variable name="x" select="50 * (./argument[1]-1)"/>
				<xsl:variable name="y" select="50 * (8-./argument[2])"/>
				
				<div>
					<xsl:attribute name="style">
						<xsl:value-of select="concat('position:absolute; left:', $x ,'px; top:', $y ,'px; width:50px; height:50px;')"/>
					</xsl:attribute>		
					<xsl:if test="./argument[3]='white'">
						<img src="/docserver/gamemaster/images/wn.gif"/>
					</xsl:if>
					<xsl:if test="./argument[3]='black'">
						<img src="/docserver/gamemaster/images/bn.gif"/>
					</xsl:if>						
				</div>				
			</xsl:for-each>
		</div>
		