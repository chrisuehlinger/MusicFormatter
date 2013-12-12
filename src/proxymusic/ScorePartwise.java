
package proxymusic;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import proxymusic.ScorePartwise.Part.Measure;
import proxymusic.guido.PartSummary;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;group ref="{}score-header"/>
 *         &lt;element name="part" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="measure" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;group ref="{}music-data"/>
 *                           &lt;attGroup ref="{}measure-attributes"/>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attGroup ref="{}part-attributes"/>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{}document-attributes"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "work",
    "movementNumber",
    "movementTitle",
    "identification",
    "defaults",
    "credit",
    "partList",
    "part"
})
@XmlRootElement(name = "score-partwise")
public class ScorePartwise {

    protected Work work;
    @XmlElement(name = "movement-number")
    protected java.lang.String movementNumber;
    @XmlElement(name = "movement-title")
    protected java.lang.String movementTitle;
    protected Identification identification;
    protected Defaults defaults;
    protected List<Credit> credit;
    @XmlElement(name = "part-list", required = true)
    protected PartList partList;
    @XmlElement(required = true)
    protected List<ScorePartwise.Part> part;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected java.lang.String version;
    
    public Note findNote(int target, int voices){
    	int index = 0;
    		for(Part p : part){
    			PartSummary ps = new PartSummary(p);
    			int partVoice = ps.countVoices();
    			for(int i=0; i<=partVoice; i++)
    			for(Measure m: p.getMeasure())
    				for(Object item: m.getNoteOrBackupOrForward())
    					if(item instanceof Note){
    						Note n=((Note)item);
    						if(Integer.parseInt(n.getVoice()) == i ){
    							index++;
    						if(index==target){
    							//System.out.println("Target: " + target + " Index: " + index);
    							return n;
    						}
    						}
    					}
    		}
    	
    	return null;
    }
    
    public java.lang.String convertToGuido() {
    	int staffConstant = 0;
    	double units2mm=0;
		java.lang.String converted = "{[\\staff<1> \\title<\"" + movementTitle + 
		"\"> \\composer<\"" + identification.getCreator().get(0).getValue() + "\", dy=4hs> \\pageFormat<";
		
		if(defaults != null){
			units2mm = defaults.getScaling().getMillimeters().doubleValue() / defaults.getScaling().getTenths().doubleValue();
			PageLayout pl = defaults.getPageLayout();
			converted += "w=" + (units2mm*pl.getPageWidth().doubleValue()) + "mm,";
			converted += "h=" + (units2mm*pl.getPageHeight().doubleValue()) + "mm,";
			PageMargins pm = pl.getPageMargins().get(0);
			converted += "tm=" + (units2mm*pm.getTopMargin().doubleValue()) + "mm,";
			converted += "bm=" + (units2mm*pm.getBottomMargin().doubleValue()) + "mm,";
			converted += "lm=" + (units2mm*pm.getLeftMargin().doubleValue()) + "mm,";
			converted += "rm=" + (units2mm*pm.getRightMargin().doubleValue()) + "mm> ";
		}
				
		converted += "\\systemFormat<\"\", dx=10hs> ]";
		
		for(Part p : part){
			p.setStaff(staffConstant);
			converted += p.convertToGuido();
			staffConstant = p.getStaff().intValue();
		}
		
		converted = converted + "}";
		
		System.out.println(converted);
		return converted;
    }

    /**
     * Gets the value of the work property.
     * 
     * @return
     *     possible object is
     *     {@link Work }
     *     
     */
    public Work getWork() {
        return work;
    }

    /**
     * Sets the value of the work property.
     * 
     * @param value
     *     allowed object is
     *     {@link Work }
     *     
     */
    public void setWork(Work value) {
        this.work = value;
    }

    /**
     * Gets the value of the movementNumber property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getMovementNumber() {
        return movementNumber;
    }

    /**
     * Sets the value of the movementNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setMovementNumber(java.lang.String value) {
        this.movementNumber = value;
    }

    /**
     * Gets the value of the movementTitle property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getMovementTitle() {
        return movementTitle;
    }

    /**
     * Sets the value of the movementTitle property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setMovementTitle(java.lang.String value) {
        this.movementTitle = value;
    }

    /**
     * Gets the value of the identification property.
     * 
     * @return
     *     possible object is
     *     {@link Identification }
     *     
     */
    public Identification getIdentification() {
        return identification;
    }

    /**
     * Sets the value of the identification property.
     * 
     * @param value
     *     allowed object is
     *     {@link Identification }
     *     
     */
    public void setIdentification(Identification value) {
        this.identification = value;
    }

    /**
     * Gets the value of the defaults property.
     * 
     * @return
     *     possible object is
     *     {@link Defaults }
     *     
     */
    public Defaults getDefaults() {
        return defaults;
    }

    /**
     * Sets the value of the defaults property.
     * 
     * @param value
     *     allowed object is
     *     {@link Defaults }
     *     
     */
    public void setDefaults(Defaults value) {
        this.defaults = value;
    }

    /**
     * Gets the value of the credit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the credit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCredit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Credit }
     * 
     * 
     */
    public List<Credit> getCredit() {
        if (credit == null) {
            credit = new ArrayList<Credit>();
        }
        return this.credit;
    }

    /**
     * Gets the value of the partList property.
     * 
     * @return
     *     possible object is
     *     {@link PartList }
     *     
     */
    public PartList getPartList() {
        return partList;
    }

    /**
     * Sets the value of the partList property.
     * 
     * @param value
     *     allowed object is
     *     {@link PartList }
     *     
     */
    public void setPartList(PartList value) {
        this.partList = value;
    }

    /**
     * Gets the value of the part property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the part property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPart().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ScorePartwise.Part }
     * 
     * 
     */
    public List<ScorePartwise.Part> getPart() {
        if (part == null) {
            part = new ArrayList<ScorePartwise.Part>();
        }
        return this.part;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getVersion() {
        if (version == null) {
            return "1.0";
        } else {
            return version;
        }
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setVersion(java.lang.String value) {
        this.version = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="measure" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;group ref="{}music-data"/>
     *                 &lt;attGroup ref="{}measure-attributes"/>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attGroup ref="{}part-attributes"/>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "measure"
    })
    public static class Part implements GuidoConvertable {

        @XmlElement(required = true)
        protected List<ScorePartwise.Part.Measure> measure;
        @XmlAttribute(required = true)
        @XmlIDREF
        @XmlSchemaType(name = "IDREF")
        protected Object id;

        /**
         * Gets the value of the measure property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the measure property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMeasure().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ScorePartwise.Part.Measure }
         * 
         * 
         */
        public List<ScorePartwise.Part.Measure> getMeasure() {
            if (measure == null) {
                measure = new ArrayList<ScorePartwise.Part.Measure>();
            }
            return this.measure;
        }

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setId(Object value) {
            this.id = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;group ref="{}music-data"/>
         *       &lt;attGroup ref="{}measure-attributes"/>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "noteOrBackupOrForward"
        })
        public static class Measure {

            @XmlElements({
                @XmlElement(name = "attributes", type = Attributes.class),
                @XmlElement(name = "direction", type = Direction.class),
                @XmlElement(name = "bookmark", type = Bookmark.class),
                @XmlElement(name = "grouping", type = Grouping.class),
                @XmlElement(name = "harmony", type = Harmony.class),
                @XmlElement(name = "sound", type = Sound.class),
                @XmlElement(name = "link", type = Link.class),
                @XmlElement(name = "note", type = Note.class),
                @XmlElement(name = "backup", type = Backup.class),
                @XmlElement(name = "figured-bass", type = FiguredBass.class),
                @XmlElement(name = "forward", type = Forward.class),
                @XmlElement(name = "barline", type = Barline.class),
                @XmlElement(name = "print", type = Print.class)
            })
            protected List<Object> noteOrBackupOrForward;
            @XmlAttribute(required = true)
            @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
            @XmlSchemaType(name = "token")
            protected java.lang.String number;
            @XmlAttribute
            protected YesNo implicit;
            @XmlAttribute(name = "non-controlling")
            protected YesNo nonControlling;
            @XmlAttribute
            protected BigDecimal width;

            /**
             * Gets the value of the noteOrBackupOrForward property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the noteOrBackupOrForward property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getNoteOrBackupOrForward().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Attributes }
             * {@link Direction }
             * {@link Bookmark }
             * {@link Grouping }
             * {@link Harmony }
             * {@link Sound }
             * {@link Link }
             * {@link Note }
             * {@link Backup }
             * {@link FiguredBass }
             * {@link Forward }
             * {@link Barline }
             * {@link Print }
             * 
             * 
             */
            public List<Object> getNoteOrBackupOrForward() {
                if (noteOrBackupOrForward == null) {
                    noteOrBackupOrForward = new ArrayList<Object>();
                }
                return this.noteOrBackupOrForward;
            }

            /**
             * Gets the value of the number property.
             * 
             * @return
             *     possible object is
             *     {@link java.lang.String }
             *     
             */
            public java.lang.String getNumber() {
                return number;
            }

            /**
             * Sets the value of the number property.
             * 
             * @param value
             *     allowed object is
             *     {@link java.lang.String }
             *     
             */
            public void setNumber(java.lang.String value) {
                this.number = value;
            }

            /**
             * Gets the value of the implicit property.
             * 
             * @return
             *     possible object is
             *     {@link YesNo }
             *     
             */
            public YesNo getImplicit() {
                return implicit;
            }

            /**
             * Sets the value of the implicit property.
             * 
             * @param value
             *     allowed object is
             *     {@link YesNo }
             *     
             */
            public void setImplicit(YesNo value) {
                this.implicit = value;
            }

            /**
             * Gets the value of the nonControlling property.
             * 
             * @return
             *     possible object is
             *     {@link YesNo }
             *     
             */
            public YesNo getNonControlling() {
                return nonControlling;
            }

            /**
             * Sets the value of the nonControlling property.
             * 
             * @param value
             *     allowed object is
             *     {@link YesNo }
             *     
             */
            public void setNonControlling(YesNo value) {
                this.nonControlling = value;
            }

            /**
             * Gets the value of the width property.
             * 
             * @return
             *     possible object is
             *     {@link BigDecimal }
             *     
             */
            public BigDecimal getWidth() {
                return width;
            }

            /**
             * Sets the value of the width property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigDecimal }
             *     
             */
            public void setWidth(BigDecimal value) {
                this.width = value;
            }

        }

        @XmlAttribute(required = false)
		private int staffConstant;
        
		@Override
		public BigInteger getStaff() {
			return BigInteger.valueOf(staffConstant);
		}
		
		public void setStaff(int value){
			staffConstant = value;
		}

		@Override
		public java.lang.String getVoice() {
			return null;
		}
		
		public class GuidoComparator implements Comparator<proxymusic.GuidoConvertable>
		{
			@Override
			public int compare(proxymusic.GuidoConvertable g1, proxymusic.GuidoConvertable g2) {
				
				int s1, s2, v1, v2;
				
				if(g1.getStaff() == null) s1=1;
				else s1=g1.getStaff().intValue();
				
				if(g2.getStaff() == null) s2=1;
				else s2=g2.getStaff().intValue();
				
				if(g1.getVoice() == null) v1=1;
				else v1=Integer.parseInt(g1.getVoice());
				
				if(g2.getVoice() == null) v2=1;
				else v2=Integer.parseInt(g2.getVoice());
				
				if(v1 < v2) return -1;
				else if(v1 > v2) return 1;
				else if(s1 < s2) return -1;
				else if(s1 > s2) return 1;
				else return 0;
			}
		}
		
		@Override
		public java.lang.String convertToGuido() {
			java.lang.String partString="";
			PartSummary ps = new PartSummary(this);
			int[] voices = ps.getVoices();
			int targetStaff = 0xffff; // initialized to a value we'll unlikely encounter
			int currentStaff = 0xffff;
			boolean notesOnly = false;
			Time currentTime = null;
			int currentDivisions=1;
			boolean currentChord=false;
			
			for (int i = 0; i < voices.length; i++) {
				int targetVoice = voices[i];
				int mainstaff = ps.getMainStaff(targetVoice);
				if (targetStaff == mainstaff) {
					notesOnly = true;
				}
				else {
					notesOnly = false;
					currentStaff = targetStaff = mainstaff;
					//fCurrentStaffIndex++;
				}

				partString = partString + ",\n[\\staff<" + (targetStaff+staffConstant) + "> ";
				if(i == 0)
					partString += "\\instr<\"" + ((ScorePart)id).getPartName().getValue() + "\", dx=-20hs, dy=-5hs> ";
				
				for(Measure m: measure){
					Note previousNote=null;
					int measureTime=0;
					partString += "\n\t(* meas. " + m.getNumber() + " *) ";
					boolean waitingBeam=false;
					for(Object item: m.getNoteOrBackupOrForward())
						if(item instanceof Note){
							Note n = (Note)item;
							if(Integer.parseInt(n.getVoice()) == targetVoice){
								
								if(previousNote != null && previousNote.getStaff().intValue() != currentStaff) {
									partString += "\\staff<" + (previousNote.getStaff().intValue()+staffConstant) + "> ";
									currentStaff = previousNote.getStaff().intValue();
								}
								
								if(previousNote != null && currentChord == false ){
									if(previousNote.getStem() != null)
										switch(previousNote.getStem().getValue()) {
										case UP:
											partString+= "\\stemsUp ";
											break;
										case DOWN:
											partString+= "\\stemsDown ";
											break;
										}
									else
										partString+= "\\stemsAuto ";
									
									if(!previousNote.getBeam().isEmpty() && previousNote.getBeam().get(0).getValue() == BeamValue.BEGIN)
											partString+= "\\beam( ";
									
									
									
								}
								
								if(currentChord == false && n.getChord() != null){
									partString += "{ ";
									currentChord=true;
								}
								
								if(previousNote != null){
									try {
									partString += previousNote.convertToGuido(currentDivisions);
									}catch(Exception e) {
										System.out.println("Issue in measure: " + m.getNumber() + " of staff: " + (currentStaff+staffConstant));
										e.printStackTrace();
										System.exit(0);
									}
									/*for(EmptyPlacement d: previousNote.getDot())
										partString += ".";*/
									
									if(currentChord == true && n.getChord() != null)
											partString += ",";
									else if(previousNote.getDuration() != null)
										measureTime+=previousNote.getDuration().intValue();
											
									partString += " ";
									
								}
								
								if(n.getChord() == null && currentChord == true){
									partString += "} ";
									currentChord=false;
									if(waitingBeam){
										partString += ") ";
										waitingBeam=false;
									}
								}
								
								if(previousNote != null
										&& !previousNote.getBeam().isEmpty()
										&& previousNote.getBeam().get(0).getValue() == BeamValue.END)
									if(currentChord==false)
										partString += ") ";
									else
										waitingBeam=true;
								
								previousNote=n;
							}
						}else if(item instanceof Attributes){
								//&& ps.getVoices(currentStaff)[0] == i+1){
							Attributes a= (Attributes)item;
							
							if(a.getDivisions() != null)
								currentDivisions= a.getDivisions().intValue();
							
							if(!a.getClef().isEmpty())
								for(Clef c: a.getClef())
									if(c.getNumber() == null || c.getNumber().intValue()==currentStaff)
										partString += c.convertToGuido();
							
							if(!a.getKey().isEmpty()){
								Key k = a.getKey().get(0);
								partString += k.convertToGuido();
							}
							
							if(!a.getTime().isEmpty()){
								Time t=a.getTime().get(0);
								partString+= t.convertToGuido();
								currentTime = t;
							}
						}
					
					if(previousNote != null){
						if(previousNote.getStem() != null)
							switch(previousNote.getStem().getValue()) {
							case UP:
								partString+= "\\stemsUp ";
								break;
							case DOWN:
								partString+= "\\stemsDown ";
								break;
							}
						else
							partString+= "\\stemsAuto ";
						try {
							partString+=previousNote.convertToGuido(currentDivisions) + " ";
							}catch(Exception e) {
								System.out.println("Issue in measure: " + m.getNumber() + " of staff: " + (currentStaff+staffConstant));
								e.printStackTrace();
								System.exit(0);
							}
						
						measureTime+=previousNote.getDuration().intValue();
					}
					
					if(currentChord){
						partString += "} ";
						currentChord = false;
						if(waitingBeam){
							partString += ") ";
							waitingBeam=false;
						}
					}
					
					if(previousNote != null
							&& !previousNote.getBeam().isEmpty()
							&& previousNote.getBeam().get(0).getValue() == BeamValue.END)
						if(currentChord==false)
							partString += ") ";
						else
							waitingBeam=true;
					
					if(measureTime != (currentDivisions*4)){
						int diff = (currentDivisions*4) - measureTime%(currentDivisions*4);
						partString+="empty*" + diff + "/" + (currentDivisions*4) + " ";
					}
					partString += "\\bar ";
					measureTime=0;
				}
				
				partString+="]";
			}
			
			staffConstant+=ps.countStaves();
			return partString;
		}

    }

}
