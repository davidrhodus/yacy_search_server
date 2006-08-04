// kelondroColumn.java
// (C) 2006 by Michael Peter Christen; mc@anomic.de, Frankfurt a. M., Germany
// first published 24.05.2006 on http://www.anomic.de
//
// This is a part of the kelondro database,
// which is a part of YaCy, a peer-to-peer based web search engine
//
// $LastChangedDate: 2006-04-02 22:40:07 +0200 (So, 02 Apr 2006) $
// $LastChangedRevision: 1986 $
// $LastChangedBy: orbiter $
//
// LICENSE
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package de.anomic.kelondro;

public class kelondroColumn {

    public static final int celltype_undefined  = 0;
    public static final int celltype_boolean    = 1;
    public static final int celltype_binary     = 2;
    public static final int celltype_string     = 3;
    public static final int celltype_cardinal   = 4;
    
    public static final int encoder_none   = 0;
    public static final int encoder_b64e   = 1;
    public static final int encoder_b256   = 2;
    public static final int encoder_bytes  = 3;
    
    private int celltype, cellwidth, encoder;
    private String nickname, description;
    
    public kelondroColumn(String nickname, int celltype, int encoder, int cellwidth, String description) {
        this.celltype = celltype;
        this.cellwidth = cellwidth;
        this.encoder = encoder;
        this.nickname = nickname;
        this.description = description;
    }

    public kelondroColumn(String celldef) {
        // define column with column syntax
        // example: <UDate-3>

        // cut quotes etc.
        celldef = celldef.trim();
        if (celldef.startsWith("<")) celldef = celldef.substring(1);
        if (celldef.endsWith(">")) celldef = celldef.substring(0, celldef.length() - 1);
        
        // parse type definition
        int p = celldef.indexOf(' ');
        String typename = "";
        if (p < 0) {
            // no typedef
            this.celltype = celltype_undefined;
            this.cellwidth = -1;
        } else {
            typename = celldef.substring(0, p);
            celldef = celldef.substring(p + 1).trim();
            
            if (typename.equals("boolean")) {
                this.celltype = celltype_boolean;
                this.cellwidth = 1;
            } else if (typename.equals("byte")) {
                this.celltype = celltype_cardinal;
                this.cellwidth = 1;
            } else if (typename.equals("short")) {
                this.celltype = celltype_cardinal;
                this.cellwidth = 2;
            } else if (typename.equals("int")) {
                this.celltype = celltype_cardinal;
                this.cellwidth = 4;
            } else if (typename.equals("long")) {
                this.celltype = celltype_cardinal;
                this.cellwidth = 8;
            } else if (typename.equals("byte[]")) {
                this.celltype = celltype_binary;
                this.cellwidth = -1; // yet undefined
            } else if (typename.equals("char")) {
                this.celltype = celltype_string;
                this.cellwidth = 1;
            } else if (typename.equals("String")) {
                this.celltype = celltype_string;
                this.cellwidth = -1; // yet undefined
            } else if (typename.equals("Cardinal")) {
                this.celltype = celltype_cardinal;
                this.cellwidth = -1; // yet undefined
            } else {
                throw new kelondroException("kelondroColumn - undefined type def '" + typename + "'");
            }           
        }
        
        // parse length
        p = celldef.indexOf('-');
        if (p < 0) {
            // if the cell was defined with a type, we dont need to give an explicit with definition
            if (this.cellwidth < 0) throw new kelondroException("kelondroColumn - no cell width definition given");
            p = celldef.indexOf(' ');
            if (p < 0) {
                this.nickname = celldef;
                celldef = "";
            } else {
                this.nickname = celldef.substring(0, p);
                celldef = celldef.substring(p + 1);
            }
        } else {
            int q = celldef.indexOf(' ');
            if (q < 0) {
                this.nickname = celldef.substring(0, p);
                try {
                    this.cellwidth = Integer.parseInt(celldef.substring(p + 1));
                } catch (NumberFormatException e) {
                    throw new kelondroException("kelondroColumn - cellwidth description wrong:" + celldef.substring(p + 1));
                }
                celldef = "";
            } else {
                this.nickname = celldef.substring(0, q);
                try {
                    this.cellwidth = Integer.parseInt(celldef.substring(p + 1, q));
                } catch (NumberFormatException e) {
                    throw new kelondroException("kelondroColumn - cellwidth description wrong:" + celldef.substring(p + 1, q));
                }
                celldef = celldef.substring(q + 1);
            }
        }
        
        // check length constraints
        if (this.cellwidth <= 0) throw new kelondroException("kelondroColumn - no cell width given for " + this.nickname);
        if (((typename.equals("boolean")) && (this.cellwidth > 1)) ||
            ((typename.equals("byte")) && (this.cellwidth > 1)) ||
            ((typename.equals("short")) && (this.cellwidth > 2)) ||
            ((typename.equals("int")) && (this.cellwidth > 4)) ||
            ((typename.equals("long")) && (this.cellwidth > 8)) ||
            ((typename.equals("char")) && (this.cellwidth > 1))
           ) throw new kelondroException("kelondroColumn - cell width " + this.cellwidth + " too wide for type " + typename);
        if (((typename.equals("short")) && (this.cellwidth <= 1)) ||
            ((typename.equals("int")) && (this.cellwidth <= 2)) ||
            ((typename.equals("long")) && (this.cellwidth <= 4))
           ) throw new kelondroException("kelondroColumn - cell width " + this.cellwidth + " not appropriate for type " + typename);
        
        // parse/check encoder type
        if ((celldef.length() > 0) && (celldef.charAt(0) == '{')) {
            p = celldef.indexOf('}');
            String expf = celldef.substring(1, p);
            celldef = celldef.substring(p + 1).trim();
                 if (expf.equals("b64e")) this.encoder = encoder_b64e;
            else if (expf.equals("b256")) this.encoder = encoder_b64e;
            else if (expf.equals("bytes")) this.encoder = encoder_b64e;
            else {
                if (this.celltype == celltype_undefined)      this.encoder = encoder_bytes;
                else if (this.celltype == celltype_boolean)   this.encoder = encoder_bytes;
                else if (this.celltype == celltype_binary)    this.encoder = encoder_bytes;
                else if (this.celltype == celltype_string)    this.encoder = encoder_bytes;
                else if (this.celltype == celltype_cardinal)  throw new kelondroException("kelondroColumn - encoder missing for cell " + this.nickname);
            }
        } else {
            if (this.celltype == celltype_cardinal) throw new kelondroException("kelondroColumn - encoder missing for cell " + this.nickname);
            this.encoder = encoder_bytes;
        }
        
        // parse/check description
        if ((celldef.length() > 0) && (celldef.charAt(0) == '"')) {
            p = celldef.indexOf('"', 1);
            this.description = celldef.substring(1, p);
            celldef = celldef.substring(p + 1).trim();
        } else {
            this.description = this.nickname;
        }
    }

    public int celltype() {
        return this.celltype;
    }
    
    public int cellwidth() {
        return this.cellwidth;
    }
    
    public int encoder() {
        return this.encoder;
    }
    
    public String nickname() {
        return this.nickname;
    }
    
    public String description() {
        return this.description;
    }
    
    public String toString() {
        StringBuffer s = new StringBuffer();
        switch (celltype) {
        case celltype_boolean:
            s.append("boolean ");
            break;
        case celltype_binary:
            s.append("byte[] ");
            break;
        case celltype_string:
            s.append("String ");
            break;
        case celltype_cardinal:
            s.append("Cardinal ");
            break;
        }
        s.append(nickname);
        s.append('-');
        s.append(cellwidth);
        s.append(' ');
        switch (encoder) {
        case encoder_b64e:
            s.append(" {b64e}");
            break;
        case encoder_b256:
            s.append(" {b256}");
            break;
        }
        return new String(s);
    }
}
