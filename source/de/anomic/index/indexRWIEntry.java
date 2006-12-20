// indexRWIEntry.java
// (C) 2006 by Michael Peter Christen; mc@anomic.de, Frankfurt a. M., Germany
// first published 20.05.2006 on http://www.anomic.de
//
// This is a part of YaCy, a peer-to-peer based web search engine
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

package de.anomic.index;

import de.anomic.kelondro.kelondroRow;
import de.anomic.kelondro.kelondroBitfield;

public interface indexRWIEntry {

    public Object clone();
    public String toPropertyForm();
    public kelondroRow.Entry toKelondroEntry();
    
    public String urlHash();
    public int quality();
    public int virtualAge();
    public long lastModified();
    public int hitcount();
    public int posintext();
    public int posinphrase();
    public int posofphrase();
    public int wordsintext();
    public int phrasesintext();
    public String getLanguage();
    public char getType();
    public kelondroBitfield flags();
    public int wordsintitle();
    public int llocal();
    public int lother();
    public int urllength();
    public int urlcomps();
    
    public void combineDistance(indexRWIEntry oe);
    public int worddistance();
    public void min(indexRWIEntry other);
    public void max(indexRWIEntry other);
    public void normalize(indexRWIEntry min, indexRWIEntry max);
    public indexRWIEntry generateNormalized(indexRWIEntry min, indexRWIEntry max);
    public boolean isNewer(indexRWIEntry other);
    public boolean isOlder(indexRWIEntry other);
   
}
